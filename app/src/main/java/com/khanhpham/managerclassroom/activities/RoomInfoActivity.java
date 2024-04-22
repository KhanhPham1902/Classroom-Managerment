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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.khanhpham.managerclassroom.R;
import com.khanhpham.managerclassroom.databinding.ActivityRoomInfoBinding;
import com.khanhpham.managerclassroom.methods.GetTimes;
import com.khanhpham.managerclassroom.methods.NotificationsHelper;
import com.khanhpham.managerclassroom.models.DamagedRoom;
import com.khanhpham.managerclassroom.models.NotifiClass;

import java.util.ArrayList;
import java.util.Locale;

public class RoomInfoActivity extends AppCompatActivity {

    ActivityRoomInfoBinding binding;
    String id, room, timeStudy, teacher, timeSignup, subject, dateStudy, floor;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String report = "";
    Dialog dialog;
    final static String rpLight = "Bóng đèn bị hư hỏng.";
    final static String rpAir = "Điều hòa bị hư hỏng.";
    final static String rpFan = "Quạt trần bị hư hỏng.";
    final static String rpProjector = "Máy chiếu bị hư hỏng.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoomInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get data from HomeFragment
        Intent getIntent = getIntent();
        id = getIntent.getStringExtra("id");
        room = getIntent.getStringExtra("room");
        timeStudy = getIntent.getStringExtra("time_study");
        dateStudy = getIntent.getStringExtra("date_study");
        timeSignup = getIntent.getStringExtra("time_signup");
        subject = getIntent.getStringExtra("subject");
        teacher = getIntent.getStringExtra("teacher");
        floor = getIntent.getStringExtra("floor");

        binding.txtRoomInfo.setText(room);
        binding.txtDateStudyInfo.setText(dateStudy);
        binding.txtSubjectInfo.setText(subject);
        binding.txtTimeSignup.setText(timeSignup);
        binding.txtTeacherInfo.setText(teacher);

        switch (timeStudy) {
            case "morning":
                binding.txtTimeInfo.setText(getText(R.string.morning_time));
                break;
            case "afternoon":
                binding.txtTimeInfo.setText(getText(R.string.afternoon_time));
                break;
            case "evening":
                binding.txtTimeInfo.setText(getText(R.string.evening_time));
                break;
        }

        // show report problems
        DatabaseReference referenceRoom = database.getReference().child("Classrooms").child(dateStudy).child(timeStudy).child(floor).child(room);
        referenceRoom.child("report").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String problem = snapshot.getValue(String.class);
                    binding.txtReportInfo.setText(problem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Button report
        binding.btnReport.setOnClickListener(v -> {
            // check date study valid
            int isTimeValid = GetTimes.isClassTimeValid(dateStudy);
            if(isTimeValid==0) {
                showAlertDialog();
            }else{
                Toast.makeText(this, getString(R.string.not_date_study), Toast.LENGTH_SHORT).show();
            }
        });

        // Button back
        binding.btnBackInfo.setOnClickListener(v -> {
            finish();
        });
    }

    // Database process
    private void databaseProcess(){
        // create damaged room database
        String timeReport = GetTimes.getTimeUpdate(RoomInfoActivity.this);
        DamagedRoom damagedRoom = new DamagedRoom(room, report, timeReport,dateStudy,timeStudy);
        DatabaseReference referenceDamaged = database.getReference().child("Damaged rooms").child(timeReport);
        referenceDamaged.setValue(damagedRoom);

        // update classroom data
        DatabaseReference referenceRoom = database.getReference().child("Classrooms").child(dateStudy).child(timeStudy).child(floor).child(room);
        referenceRoom.child("report").setValue(report);
        referenceRoom.child("status").setValue(2);

        // update user data
        DatabaseReference referenceUser = database.getReference().child("users").child(id);
        referenceUser.child("data").child(timeSignup).child("report").setValue(report);
        referenceUser.child("data").child(timeSignup).child("status").setValue(2);

        Toast.makeText(RoomInfoActivity.this, getText(R.string.saved), Toast.LENGTH_SHORT).show();
        NotificationsHelper.createNotifications(RoomInfoActivity.this, "REPORT_PROBLEM",
                getString(R.string.noti), getString(R.string.classroom) + " " + room + " " + getString(R.string.has_damaged));

        // user notification
        String time_noti = GetTimes.getTimeUpdate(RoomInfoActivity.this);
        String desc_noti = getString(R.string.classroom) + " " + room + " " + getString(R.string.has_damaged);
        NotifiClass notifiClass = new NotifiClass(desc_noti, time_noti, 2);
        referenceUser.child("notifications").child(time_noti).setValue(notifiClass);

        dialog.dismiss();
        finish();
    }

    // Report dialog
    private void showAlertDialog() {
        if(!isFinishing()) {
            if (dialog == null) {
                dialog = new Dialog(RoomInfoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            dialog.setContentView(R.layout.report_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button btnDialog = dialog.findViewById(R.id.btnConfirmReport);
            Button btnSkip = dialog.findViewById(R.id.btnSkipReport);
            CheckBox cbLight = dialog.findViewById(R.id.cbLight);
            CheckBox cbAirCondi = dialog.findViewById(R.id.cbAirCondi);
            CheckBox cbFan = dialog.findViewById(R.id.cbFan);
            CheckBox cbProjector = dialog.findViewById(R.id.cbProjector);
            CheckBox cbOthers = dialog.findViewById(R.id.cbOthers);
            EditText edtReport = dialog.findViewById(R.id.edtReport);

            // checkbox tick
            cbLight.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkingCheckbox(isChecked,rpLight,cbOthers);
            });
            cbAirCondi.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkingCheckbox(isChecked,rpAir,cbOthers);
            });
            cbFan.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkingCheckbox(isChecked,rpFan,cbOthers);
            });
            cbProjector.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkingCheckbox(isChecked,rpProjector,cbOthers);
            });
            cbOthers.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    edtReport.setVisibility(View.VISIBLE);
                    cbLight.setEnabled(false);
                    cbFan.setEnabled(false);
                    cbAirCondi.setEnabled(false);
                    cbProjector.setEnabled(false);
                } else {
                    edtReport.setVisibility(View.GONE);
                    report = "";
                    cbLight.setEnabled(true);
                    cbFan.setEnabled(true);
                    cbAirCondi.setEnabled(true);
                    cbProjector.setEnabled(true);
                }
            });

            // get problems report from edit text
            edtReport.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    String otherProblem = edtReport.getText().toString();
                    if (cbOthers.isChecked() && !otherProblem.isEmpty()) {
                        report = otherProblem;
                    }
                }
            });

            // Confirm report problems
            btnDialog.setOnClickListener(v -> {
                if (cbLight.isChecked() || cbFan.isChecked() || cbAirCondi.isChecked() || cbProjector.isChecked() || cbOthers.isChecked()) {
                    databaseProcess();
                } else {
                    Toast.makeText(this, getText(R.string.choose_problem), Toast.LENGTH_SHORT).show();
                }
            });

            // Skip
            btnSkip.setOnClickListener(v -> {
                dialog.dismiss();
            });

            dialog.show();
        }
    }

    // Checkbox checking
    private void checkingCheckbox(boolean isChecked, String device, CheckBox checkBox){
        if (isChecked) {
            if (!report.equals(device))
                report += device;
            checkBox.setEnabled(false);
        } else {
            report = report.replace(device, "");
            checkBox.setEnabled(true);
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
}