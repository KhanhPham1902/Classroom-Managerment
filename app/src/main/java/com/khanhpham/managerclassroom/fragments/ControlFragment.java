package com.khanhpham.managerclassroom.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khanhpham.managerclassroom.R;
import com.khanhpham.managerclassroom.databinding.FragmentControlBinding;
import com.khanhpham.managerclassroom.methods.GetTimes;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.Objects;

public class ControlFragment extends Fragment {

    private FragmentControlBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    int status;
    String id, time_study, floor, room, date_study, time_signup;
    Boolean status_light = false, status_air = false, status_fan = false, status_projector = false;
    Boolean device1 = false, device2 = false, device3 = false, device4 = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentControlBinding.inflate(inflater, container, false);

        // get data from ListRoomFragment
        assert getArguments() != null;
        id = getArguments().getString("id");
        status = getArguments().getInt("status");
        time_study = getArguments().getString("time_study");
        time_signup = getArguments().getString("time_signup");
        date_study = getArguments().getString("date_study");
        floor = getArguments().getString("floor");
        room = getArguments().getString("room");

        assert time_study != null;
        switch (time_study) {
            case "morning":
                binding.txtFloorRoom.setText(getText(R.string.time_study)+": "+date_study+"\n"+getText(R.string.study_time)+": "+getText(R.string.morning_time)+"\n"+getText(R.string.classroom)+": " + room);
                break;
            case "afternoon":
                binding.txtFloorRoom.setText(getText(R.string.time_study)+": "+date_study+"\n"+getText(R.string.study_time)+": "+getText(R.string.afternoon_time)+"\n"+getText(R.string.classroom)+": " + room);
                break;
            case "evening":
                binding.txtFloorRoom.setText(getText(R.string.time_study)+": "+date_study+"\n"+getText(R.string.study_time)+": "+getText(R.string.evening_time)+"\n"+getText(R.string.classroom)+": " + room);
                break;
        }

        // check status device
        checkStatusDevice("light", "layoutLight", "imgLight", "txtLight", R.drawable.light_on, R.drawable.light_off, "device1");
        checkStatusDevice("fan", "layoutFan", "imgFan", "txtFan", R.drawable.fan_on, R.drawable.fan_off, "device3");
        checkStatusDevice("projector", "layoutProjector", "imgProjector", "txtProjector", R.drawable.projector_on, R.drawable.projector_off, "device4");
        checkStatusDevice("air_conditioner", "layoutAirCondition", "imgAirConditioner", "txtAirConditioner", R.drawable.amplifier, R.drawable.amplifier_off, "device2");

        // check date study valid
        int isTimeValid;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            isTimeValid = GetTimes.isTimeValid(date_study);
        } else {
            isTimeValid = 0;
        }

        // card view light click
        binding.imgLight.setOnClickListener(v -> {
            status_light = !status_light;
            device1 = !device1;
            controlDevice("light",status_light,isTimeValid,"device1",device1);
        });

        // card view air-conditioner click
        binding.imgAirConditioner.setOnClickListener(v -> {
            status_air = !status_air;
            device2 = !device2;
            controlDevice("air_conditioner",status_air,isTimeValid,"device2",device2);
        });

        // card view fan click
        binding.imgFan.setOnClickListener(v -> {
            status_fan = !status_fan;
            device3 = !device3;
            controlDevice("fan",status_fan,isTimeValid,"device3",device3);
        });

        // card view projector click
        binding.imgProjector.setOnClickListener(v -> {
            status_projector = !status_projector;
            device4 = !device4;
            controlDevice("projector",status_projector,isTimeValid,"device4",device4);
        });

        // turn on/ off all device
        binding.swAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    turnOnAllDevices(isTimeValid);
                    binding.layoutTurnAll.setBackgroundResource(R.color.blur_blue);
                    binding.txtTurnAll.setText(getString(R.string.off_all));
                }else{
                    turnOffAllDevices(isTimeValid);
                    binding.layoutTurnAll.setBackgroundResource(R.color.blur_black);
                    binding.txtTurnAll.setText(getString(R.string.on_all));
                }
            }
        });

        // Button back
        binding.btnBackControl.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            }
        });

        return binding.getRoot();
    }

    // control device
    private void controlDevice(String device, boolean statusDevice, int isValid, String demo, boolean statusDemo) {
        if (!date_study.isEmpty() && status!=0 && !time_study.isEmpty() && !floor.isEmpty() && !room.isEmpty()) {
            DatabaseReference reference = database.getReference().child("Classrooms").child(date_study).child(time_study).child(floor).child(room).child("deviceClassroom");
            DatabaseReference referenceUser = database.getReference().child("users").child(id).child("data").child(time_signup).child("deviceClassroom");
            DatabaseReference referenceDevices = database.getReference().child("devices");

            if (status != 2 && isValid==0) {
                reference.child(device).setValue(statusDevice);
                referenceUser.child(device).setValue(statusDevice);
                referenceDevices.child(demo).setValue(statusDemo);
            } else if(status==2){
                Toast.makeText(requireContext(), getString(R.string.damaged_device), Toast.LENGTH_SHORT).show();
            } else if(isValid<0){
                Toast.makeText(requireContext(), getString(R.string.not_date_study), Toast.LENGTH_SHORT).show();
            }

//            if(status != 2){
//                reference.child(device).setValue(statusDevice);
//                referenceUser.child(device).setValue(statusDevice);
//                referenceDevices.child(demo).setValue(statusDemo);
//            }else{
//                Toast.makeText(requireContext(), getString(R.string.damaged_device), Toast.LENGTH_SHORT).show();
//            }
        }
    }

    // Turn on all devices
    private void turnOnAllDevices(int isValid){
        if (!date_study.isEmpty() && status!=0 && !time_study.isEmpty() && !floor.isEmpty() && !room.isEmpty()) {
            DatabaseReference reference = database.getReference().child("Classrooms").child(date_study).child(time_study).child(floor).child(room).child("deviceClassroom");
            DatabaseReference referenceUser = database.getReference().child("users").child(id).child("data").child(time_signup).child("deviceClassroom");
            DatabaseReference referenceDevices = database.getReference().child("devices");

            if (status != 2 && isValid==0) {
                reference.child("light").setValue(true);
                reference.child("air_conditioner").setValue(true);
                reference.child("fan").setValue(true);
                reference.child("projector").setValue(true);

                referenceUser.child("light").setValue(true);
                referenceUser.child("air_conditioner").setValue(true);
                referenceUser.child("fan").setValue(true);
                referenceUser.child("projector").setValue(true);

                referenceDevices.child("device1").setValue(true);
                referenceDevices.child("device2").setValue(true);
                referenceDevices.child("device3").setValue(true);
                referenceDevices.child("device4").setValue(true);
                referenceDevices.child("all").setValue(true);
            } else if(status==2){
                Toast.makeText(requireContext(), getString(R.string.damaged_device), Toast.LENGTH_SHORT).show();
            } else if(isValid<0){
                Toast.makeText(requireContext(), getString(R.string.not_date_study), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Turn off all devices
    private void turnOffAllDevices(int isValid){
        if (!date_study.isEmpty() && status!=0 && !time_study.isEmpty() && !floor.isEmpty() && !room.isEmpty()) {
            DatabaseReference reference = database.getReference().child("Classrooms").child(date_study).child(time_study).child(floor).child(room).child("deviceClassroom");
            DatabaseReference referenceUser = database.getReference().child("users").child(id).child("data").child(time_signup).child("deviceClassroom");
            DatabaseReference referenceDevices = database.getReference().child("devices");

            if (status != 2 && isValid==0) {
                reference.child("light").setValue(false);
                reference.child("air_conditioner").setValue(false);
                reference.child("fan").setValue(false);
                reference.child("projector").setValue(false);

                referenceUser.child("light").setValue(false);
                referenceUser.child("air_conditioner").setValue(false);
                referenceUser.child("fan").setValue(false);
                referenceUser.child("projector").setValue(false);

                referenceDevices.child("device1").setValue(false);
                referenceDevices.child("device2").setValue(false);
                referenceDevices.child("device3").setValue(false);
                referenceDevices.child("device4").setValue(false);
                referenceDevices.child("all").setValue(false);
            } else if(status==2){
                Toast.makeText(requireContext(), getString(R.string.damaged_device), Toast.LENGTH_SHORT).show();
            } else if(isValid<0){
                Toast.makeText(requireContext(), getString(R.string.not_date_study), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // check status device
    private void checkStatusDevice(String device, String layoutId, String imgId, String txtId, int imgIdOn, int imgIdOff, String demo) {
        LinearLayout layout = getLayout(binding, layoutId);
        ImageView img = getImageView(binding, imgId);
        TextView txt = getTextView(binding, txtId);

        DatabaseReference reference = database.getReference().child("Classrooms").child(date_study).child(time_study).child(floor).child(room).child("deviceClassroom");
        DatabaseReference referenceDevices = database.getReference().child("devices");
        referenceDevices.child(demo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean status = snapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(status)) {
                    if (isAdded()) {
                        img.setImageResource(imgIdOn);
                        layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.select));
                        txt.setText(getText(R.string.on));
                    }
                } else {
                    if (isAdded()) {
                        img.setImageResource(imgIdOff);
                        layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blank));
                        txt.setText(getText(R.string.off));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get linearlayout id
    private LinearLayout getLayout(FragmentControlBinding binding, String layoutId) {
        int resourceId = binding.getRoot().getResources().getIdentifier(layoutId, "id", requireContext().getPackageName());
        return binding.getRoot().findViewById(resourceId);
    }

    // get textview id
    private TextView getTextView(FragmentControlBinding binding, String textId) {
        int resourceId = binding.getRoot().getResources().getIdentifier(textId, "id", requireContext().getPackageName());
        return binding.getRoot().findViewById(resourceId);
    }

    // get imageview id
    private ImageView getImageView(FragmentControlBinding binding, String imgId) {
        int resourceId = binding.getRoot().getResources().getIdentifier(imgId, "id", requireContext().getPackageName());
        return binding.getRoot().findViewById(resourceId);
    }
}