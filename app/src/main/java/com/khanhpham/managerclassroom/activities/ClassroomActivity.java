package com.khanhpham.managerclassroom.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khanhpham.managerclassroom.R;
import com.khanhpham.managerclassroom.adapter.SubjectAdapter;
import com.khanhpham.managerclassroom.databinding.ActivityClassroomBinding;
import com.khanhpham.managerclassroom.methods.GetTimes;
import com.khanhpham.managerclassroom.methods.NotificationsHelper;
import com.khanhpham.managerclassroom.models.Classroom;
import com.khanhpham.managerclassroom.models.DamagedRoom;
import com.khanhpham.managerclassroom.models.DatabaseUpdateListener;
import com.khanhpham.managerclassroom.models.DeviceClassroom;
import com.khanhpham.managerclassroom.models.ListSubject;
import com.khanhpham.managerclassroom.models.NotifiClass;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ClassroomActivity extends AppCompatActivity {

    ActivityClassroomBinding classroomBinding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private int status = 0;
    private String id, date_study, floorName, roomName, time_study, time_signup, teacher, subject, report, damagedRoom;
    int floor13 = 0, floor14 = 0, floor15 = 0, floor16 = 0;
    Dialog dialog;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classroomBinding = ActivityClassroomBinding.inflate(getLayoutInflater());
        setContentView(classroomBinding.getRoot());

        // Get data from HomeFragment
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        time_study = intent.getStringExtra("time_study");
        date_study = intent.getStringExtra("date_study");
        Log.d("ID", id);

        assert time_study != null;
        switch (time_study) {
            case "morning":
                classroomBinding.txtTime.setText(date_study + "\n" + getText(R.string.morning_time));
                break;
            case "afternoon":
                classroomBinding.txtTime.setText(date_study + "\n" + getText(R.string.afternoon_time));
                break;
            case "evening":
                classroomBinding.txtTime.setText(date_study + "\n" + getText(R.string.evening_time));
                break;
        }

        String[] floors = {"13", "14", "15", "16"};
        String[] rooms = {"01", "02", "03", "04", "05", "06"};

        for (String floor : floors) {
            for (String room : rooms) {
                String roomNumber = floor + room;

                // Check status rooms
                LinearLayout layout = getLayout(classroomBinding, "layoutP" + roomNumber);
                TextView txt = getTextView(classroomBinding, "txtP" + roomNumber);
                checkStatusClassroom(floor, roomNumber, layout, txt);

                // check damaged room
                checkDamagedRoom(floor, roomNumber);

                // Room item click
                String cvId = "cvRoom" + roomNumber;
                CardView cardView = getCardView(classroomBinding, cvId);
                cardView.setOnClickListener(v -> {
                    onClickItemRoom(floor, roomNumber);
                    floorName = floor;
                    roomName = roomNumber;
                });
            }
        }

        // cardview floor13 click
        classroomBinding.cvFloor13.setOnClickListener(v -> {
            floor13++;
            LinearLayout layout = getLayout(classroomBinding, "layoutListRoom13");
            ImageView img = getImageView(classroomBinding, "imgDrop13");
            onClickItemFloor(floor13, layout, img);
        });

        // cardview floor14 click
        classroomBinding.cvFloor14.setOnClickListener(v -> {
            floor14++;
            LinearLayout layout = getLayout(classroomBinding, "layoutListRoom14");
            ImageView img = getImageView(classroomBinding, "imgDrop14");
            onClickItemFloor(floor14, layout, img);
        });

        // cardview floor15 click
        classroomBinding.cvFloor15.setOnClickListener(v -> {
            floor15++;
            LinearLayout layout = getLayout(classroomBinding, "layoutListRoom15");
            ImageView img = getImageView(classroomBinding, "imgDrop15");
            onClickItemFloor(floor15, layout, img);
        });

        // cardview floor16 click
        classroomBinding.cvFloor16.setOnClickListener(v -> {
            floor16++;
            LinearLayout layout = getLayout(classroomBinding, "layoutListRoom16");
            ImageView img = getImageView(classroomBinding, "imgDrop16");
            onClickItemFloor(floor16, layout, img);
        });

        // back
        classroomBinding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    // Check room is damaged?
    private void checkDamagedRoom(String floor,String room) {
        DatabaseReference referenceDamaged = database.getReference().child("Damaged rooms");
        referenceDamaged.addValueEventListener (new  ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        DamagedRoom damagedRoom = dataSnapshot.getValue(DamagedRoom.class);
                        assert damagedRoom != null;
                        String roomName = damagedRoom.getRoom();
                        if(room.equals(roomName)){
                            String problem = damagedRoom.getReport();
                            createClassroom(2,floor,room,problem);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassroomActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // create classroom
    private void createClassroom(int status, String floor, String room, String report) {
        DeviceClassroom deviceClassroom = new DeviceClassroom(false, false, false, false);
        Classroom classroom = new Classroom(status, floorName, roomName, date_study, time_study, "", "", "", report, deviceClassroom);
        database.getReference().child("Classrooms").child(date_study).child(time_study).child(floor).child(room).setValue(classroom);
    }

    // Click item to select room
    private void onClickItemRoom(String floor, String room) {
        DatabaseReference refDate = database.getReference().child("Classrooms").child(date_study).child(time_study).child(floor).child(room);
        valueEventListener  = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    createClassroom(0, floor, room, "");
                    showAlertDialog(floor, room);
                } else {
                    Integer status = snapshot.child("status").getValue(Integer.class);
                    if (status == 0) { // empty room
                        showAlertDialog(floor, room);
                    } else if (status == 1) { // used room
                        Toast.makeText(ClassroomActivity.this, getText(R.string.room_full), Toast.LENGTH_SHORT).show();
                    } else if (status == 2) { // repair room
                        Toast.makeText(ClassroomActivity.this, getText(R.string.room_repair), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassroomActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        refDate.addListenerForSingleValueEvent(valueEventListener);
    }

    // Check status rooms
    private void checkStatusClassroom(String floor, String room, LinearLayout layout, TextView txt) {
        DatabaseReference reference = database.getReference().child("Classrooms").child(date_study).child(time_study).child(floor).child(room);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer status = snapshot.child("status").getValue(Integer.class);
                    if (status == 0) { // empty room
                        layout.setBackgroundColor(ContextCompat.getColor(ClassroomActivity.this, R.color.blank));
                        txt.setTextColor(ContextCompat.getColor(ClassroomActivity.this, R.color.blank));
                    } else if (status == 1) { // used room
                        layout.setBackgroundColor(ContextCompat.getColor(ClassroomActivity.this, R.color.select));
                        txt.setTextColor(ContextCompat.getColor(ClassroomActivity.this, R.color.select));
                    } else if (status == 2) { // damaged room
                        layout.setBackgroundColor(ContextCompat.getColor(ClassroomActivity.this, R.color.damaged));
                        txt.setTextColor(ContextCompat.getColor(ClassroomActivity.this, R.color.damaged));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassroomActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get linearlayout id
    private LinearLayout getLayout(ActivityClassroomBinding binding, String layoutId) {
        int resourceId = getResources().getIdentifier(layoutId, "id", getPackageName());
        return binding.getRoot().findViewById(resourceId);
    }

    // get textview id
    private TextView getTextView(ActivityClassroomBinding binding, String textId) {
        int resourceId = getResources().getIdentifier(textId, "id", getPackageName());
        return binding.getRoot().findViewById(resourceId);
    }

    // get imageview id
    private ImageView getImageView(ActivityClassroomBinding binding, String imgId) {
        int resourceId = getResources().getIdentifier(imgId, "id", getPackageName());
        return binding.getRoot().findViewById(resourceId);
    }

    // get card view id
    private CardView getCardView(ActivityClassroomBinding binding, String cvId) {
        int resourceId = getResources().getIdentifier(cvId, "id", getPackageName());
        return binding.getRoot().findViewById(resourceId);
    }

    // Confirm dialog
    private void showAlertDialog(String floor, String room) {
        if(!isFinishing()) {
            if (dialog == null) {
                dialog = new Dialog(ClassroomActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            dialog.setContentView(R.layout.confirm_select);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView txtDescDialog = dialog.findViewById(R.id.txtDescDialogRegis);
            EditText edtSubject = dialog.findViewById(R.id.edtSubject);
            Button btnDialog = dialog.findViewById(R.id.btnDialogRegis);
            Button btnSkip = dialog.findViewById(R.id.btnDoneRegis);
            Spinner spinner = dialog.findViewById(R.id.spinner);

            txtDescDialog.setText(getText(R.string.room_desc));

            selectSubject(spinner, edtSubject);

            // Confirm select this room
            btnDialog.setOnClickListener(v -> {
                if (subject != null) {
                    updateDatabase(subject, floor, room, new DatabaseUpdateListener() {
                        @Override
                        public void onUpdateSuccess() {
                            dialog.dismiss();
                        }

                        @Override
                        public void onUpdateFailure(String errorMessage) {
                            Toast.makeText(ClassroomActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, getText(R.string.subject_warning), Toast.LENGTH_SHORT).show();
                }
            });

            // Skip
            btnSkip.setOnClickListener(v -> {
                dialog.dismiss();
            });

            dialog.show();
        }
    }

    // Select subject from spinner
    private void selectSubject(Spinner spinner, EditText edtSubject) {
        SubjectAdapter subjectAdapter = new SubjectAdapter(getApplicationContext(), ListSubject.getSubjectList(this));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = parent.getItemAtPosition(position).toString();
                if (subject.equals(getString(R.string.others))) {
                    edtSubject.setVisibility(View.VISIBLE);
                } else {
                    edtSubject.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // update data from edittext
        edtSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                subject = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spinner.setAdapter(subjectAdapter);
    }


    // Update room and user data
    private void updateDatabase(String subject, String floor, String room, DatabaseUpdateListener listener) {
        time_signup = GetTimes.getTimeUpdate(ClassroomActivity.this);
        status = 1;
        DatabaseReference referenceUser = database.getReference().child("users").child(id);
        DatabaseReference referenceRoom = database.getReference().child("Classrooms").child(date_study).child(time_study).child(floor).child(room);
        Classroom classroom = new Classroom(status, floorName, roomName, date_study, time_study, time_signup, teacher, subject, "", new DeviceClassroom(false, false, false, false));
        referenceRoom.setValue(classroom).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                NotificationsHelper.createNotifications(ClassroomActivity.this, "REGISTER", getString(R.string.noti), getString(R.string.select_success));
                // user notification
                String desc_noti = getString(R.string.noti_register) + " " + roomName;
                NotifiClass notifiClass = new NotifiClass(desc_noti, time_signup, 1);
                referenceUser.child("notifications").child(time_signup).setValue(notifiClass);
                Toast.makeText(ClassroomActivity.this, getText(R.string.select_success), Toast.LENGTH_SHORT).show();
                listener.onUpdateSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onUpdateFailure(e.getMessage());
            }
        });

        // set username to Classrooms database
        referenceUser.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    teacher = snapshot.child("username").getValue(String.class);
                    // Create classroom and user data
                    referenceRoom.child("teacher").setValue(teacher);
                    Classroom classroom = new Classroom(status, floorName, roomName, date_study, time_study, time_signup, teacher, subject, "", new DeviceClassroom(false, false, false, false));
                    referenceUser.child("data").child(time_signup).setValue(classroom);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassroomActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Click item floor
    private void onClickItemFloor(int count, LinearLayout layout, ImageView img) {
        if (count % 2 != 0) {
            layout.setVisibility(View.VISIBLE);
            img.setImageResource(R.drawable.dropup);
        } else {
            layout.setVisibility(View.GONE);
            img.setImageResource(R.drawable.drop_down);
        }
    }

    // close dialog
    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // disconnect to firebase
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(floorName!=null && roomName!=null) {
            DatabaseReference classroomRef = database.getReference().child("Classrooms").child(date_study).child(time_study).child(floorName).child(roomName);
            classroomRef.removeEventListener(valueEventListener);
        }
    }
}