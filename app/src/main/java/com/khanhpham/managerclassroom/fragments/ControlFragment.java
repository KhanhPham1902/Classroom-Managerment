package com.khanhpham.managerclassroom.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                binding.txtFloorRoom.setText(date_study+"\n"+getText(R.string.study_time)+": "+getText(R.string.morning_time)+"\n"+getText(R.string.classroom)+": " + room);
                break;
            case "afternoon":
                binding.txtFloorRoom.setText(date_study+"\n"+getText(R.string.study_time)+": "+getText(R.string.afternoon_time)+"\n"+getText(R.string.classroom)+": " + room);
                break;
            case "evening":
                binding.txtFloorRoom.setText(date_study+"\n"+getText(R.string.study_time)+": "+getText(R.string.evening_time)+"\n"+getText(R.string.classroom)+": " + room);
                break;
        }

        // check status device
        checkStatusDevice("light", "layoutLight", "imgLight", "txtLight", R.drawable.light_on, R.drawable.light_off);
        checkStatusDevice("fan", "layoutFan", "imgFan", "txtFan", R.drawable.fan_on, R.drawable.fan_off);
        checkStatusDevice("projector", "layoutProjector", "imgProjector", "txtProjector", R.drawable.projector_on, R.drawable.projector_off);
        checkStatusDevice("air_conditioner", "layoutAirCondition", "imgAirConditioner", "txtAirConditioner", R.drawable.air_conditioner_on, R.drawable.air_conditioner_off);

        // check date study valid
        int isTimeValid = GetTimes.isClassTimeValid(date_study);

        // card view light click
        binding.imgLight.setOnClickListener(v -> {
            status_light = !status_light;
            controlDevice("light",status_light,isTimeValid);
        });

        // card view air-conditioner click
        binding.imgAirConditioner.setOnClickListener(v -> {
            status_air = !status_air;
            controlDevice("air_conditioner",status_air,isTimeValid);
        });

        // card view fan click
        binding.imgFan.setOnClickListener(v -> {
            status_fan = !status_fan;
            controlDevice("fan",status_fan,isTimeValid);
        });

        // card view projector click
        binding.imgProjector.setOnClickListener(v -> {
            status_projector = !status_projector;
            controlDevice("projector",status_projector,isTimeValid);
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
    private void controlDevice(String device, boolean statusDevice, int isValid) {
        if (!date_study.isEmpty() && status!=0 && !time_study.isEmpty() && !floor.isEmpty() && !room.isEmpty()) {
            DatabaseReference reference = database.getReference().child("Classrooms").child(date_study).child(time_study).child(floor).child(room).child("deviceClassroom");
            DatabaseReference referenceUser = database.getReference().child("users").child(id).child("data").child(time_signup).child("deviceClassroom");

            if (status != 2 && isValid==0) {
                reference.child(device).setValue(statusDevice);
                referenceUser.child(device).setValue(statusDevice);
            } else if(status==2){
                Toast.makeText(requireContext(), getString(R.string.damaged_device), Toast.LENGTH_SHORT).show();
            } else if(isValid<0){
                Toast.makeText(requireContext(), getString(R.string.not_date_study), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // check status device
    private void checkStatusDevice(String device, String layoutId, String imgId, String txtId, int imgIdOn, int imgIdOff) {
        LinearLayout layout = getLayout(binding, layoutId);
        ImageView img = getImageView(binding, imgId);
        TextView txt = getTextView(binding, txtId);

        DatabaseReference reference = database.getReference().child("Classrooms").child(date_study).child(time_study).child(floor).child(room).child("deviceClassroom");
        reference.child(device).addValueEventListener(new ValueEventListener() {
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