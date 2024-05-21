package com.khanhpham.managerclassroom.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khanhpham.managerclassroom.R;
import com.khanhpham.managerclassroom.activities.ClassroomActivity;
import com.khanhpham.managerclassroom.activities.RoomInfoActivity;
import com.khanhpham.managerclassroom.adapter.RoomAdapter;
import com.khanhpham.managerclassroom.methods.GetTimes;
import com.khanhpham.managerclassroom.methods.NotificationsHelper;
import com.khanhpham.managerclassroom.models.Classroom;
import com.khanhpham.managerclassroom.models.DatabaseUpdateListener;
import com.khanhpham.managerclassroom.models.NotifiClass;
import com.khanhpham.managerclassroom.models.OnItemClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment implements OnItemClickListener {
    private ArrayList<Classroom> classroomArrayList;
    private RoomAdapter roomAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView rvRoom;
    SwipeRefreshLayout swiperefreshlayout;
    LinearLayout layoutHome;
    TextView textView, txtTimeHome, txtCheckRoom;
    CardView cvTime;
    String id;
    private String date_study;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        cvTime = view.findViewById(R.id.cvSelectTime);
        swiperefreshlayout = view.findViewById(R.id.swipeRefreshLayout);
        layoutHome = view.findViewById(R.id.layoutHome);
        textView = view.findViewById(R.id.txtHome);
        txtTimeHome = view.findViewById(R.id.txtTimeHome);
        txtCheckRoom = view.findViewById(R.id.txtCheckRoom);
        rvRoom = view.findViewById(R.id.recycleViewHome);

        // set up recycle view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvRoom.setLayoutManager(gridLayoutManager);

        classroomArrayList = new ArrayList<>();
        roomAdapter = new RoomAdapter(classroomArrayList, getContext(), this);
        rvRoom.setAdapter(roomAdapter);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 0 && hour <= 4) {
            textView.setVisibility(View.GONE);
        } else if (hour >= 5 && hour <= 12) { // morning
            textView.setText(getText(R.string.good_morning));
        } else if (hour >= 13 && hour <= 18) { // afternoon
            textView.setText(getText(R.string.good_afternoon));
        } else if (hour >= 19 && hour <= 23) { // evening
            textView.setText(getText(R.string.good_evening));
        }

        // get data from MainActivity
        if (getArguments() != null) {
            id = getArguments().getString("id");
        }

        // Update data when swiping from top to bottom
        swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                databaseProcess();
            }
        });

        // Database process
        databaseProcess();

        // Calendar click
        cvTime.setOnClickListener(v -> {
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            CalendarConstraints.DateValidator validator = DateValidatorPointForward.now();
            constraintsBuilder.setValidator(validator);

            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getText(R.string.select_day))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    date_study = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection));
                    txtTimeHome.setText(date_study);
                    showTimeDialog();
                }
            });

            // show DatePicker
            materialDatePicker.show(requireActivity().getSupportFragmentManager(), "tag");
        });

        return view;
    }

    // Database process
    private void databaseProcess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout);
        AlertDialog dialog = builder.create();

        DatabaseReference reference = database.getReference().child("users").child(id).child("data");
        dialog.show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    txtCheckRoom.setVisibility(View.GONE);
                    classroomArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Classroom classroom = dataSnapshot.getValue(Classroom.class);
                        assert classroom != null;

                        int isTimeValid = GetTimes.isClassTimeValid(classroom.getDate_study());
                        if (isTimeValid <= 0) {
                            classroomArrayList.add(0, classroom);
                        } else {
                            // remove from database
                            dataSnapshot.getRef().removeValue();
                            database.getReference().child("Classrooms").child(classroom.getDate_study()).removeValue();
                        }
                    }
                    roomAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    txtCheckRoom.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
                swiperefreshlayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swiperefreshlayout.setRefreshing(false);
                dialog.dismiss();
            }
        });
    }

    // check class time valid
    private int isClassTimeValid(String classDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyyy", Locale.getDefault());
            Date currentDate = new Date();
            String formattedCurrentDate = sdf.format(currentDate);
            Date currentDateFormatted = sdf.parse(formattedCurrentDate);
            Date classDateFormatted = sdf.parse(classDate);
            assert currentDateFormatted != null;
            return currentDateFormatted.compareTo(classDateFormatted);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // send data
    private void sendData(String time) {
        Intent intent = new Intent(getActivity(), ClassroomActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("time_study", time);
        intent.putExtra("date_study", date_study);
        startActivity(intent);
    }

    // time study dialog
    private void showTimeDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.time_study_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CardView cvMorning = dialog.findViewById(R.id.cvMorning);
        CardView cvAfternoon = dialog.findViewById(R.id.cvAfternoon);
        CardView cvEvening = dialog.findViewById(R.id.cvEvening);

        // get current date
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date currentDate = new Date();
        String formattedCurrentDate = sdfDate.format(currentDate);
        // get current time
        LocalTime currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalTime.now();

            if(formattedCurrentDate.compareTo(date_study)==0) {
                if (currentTime.isBefore(LocalTime.of(6,30))) {
                    cvMorning.setVisibility(View.VISIBLE);
                    cvAfternoon.setVisibility(View.VISIBLE);
                    cvEvening.setVisibility(View.VISIBLE);
                } else if (currentTime.isBefore(LocalTime.of(12,30))) {
                    cvMorning.setVisibility(View.GONE);
                    cvAfternoon.setVisibility(View.VISIBLE);
                    cvEvening.setVisibility(View.VISIBLE);
                } else if (currentTime.isBefore(LocalTime.of(17,30))) {
                    cvMorning.setVisibility(View.GONE);
                    cvAfternoon.setVisibility(View.GONE);
                    cvEvening.setVisibility(View.VISIBLE);
                } else if (currentTime.isBefore(LocalTime.of(22, 0))) {
                    cvMorning.setVisibility(View.GONE);
                    cvAfternoon.setVisibility(View.GONE);
                    cvEvening.setVisibility(View.GONE);
                }
            }
        }

        // select morning
        cvMorning.setOnClickListener(v -> {
            sendData("morning");
            dialog.dismiss();
        });

        // select afternoon
        cvAfternoon.setOnClickListener(v -> {
            sendData("afternoon");
            dialog.dismiss();
        });

        // select evening
        cvEvening.setOnClickListener(v -> {
            sendData("evening");
            dialog.dismiss();
        });

        dialog.show();
    }

    // Remove classroom from database
    private void removeRoom(int position, DatabaseUpdateListener listener){
        String timeSignup = classroomArrayList.get(position).getTime_signup();
        String dateStudy = classroomArrayList.get(position).getDate_study();
        String timeStudy = classroomArrayList.get(position).getTime_study();
        String floor = classroomArrayList.get(position).getFloorName();
        String room = classroomArrayList.get(position).getRoomName();
        DatabaseReference referenceUser = database.getReference().child("users").child(id);
        DatabaseReference referenceRoom = database.getReference().child("Classrooms").child(dateStudy).child(timeStudy).child(floor).child(room);
        referenceRoom.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                referenceUser.child("data").child(timeSignup).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(requireContext(), getString(R.string.cancel_success), Toast.LENGTH_SHORT).show();
                        NotificationsHelper.createNotifications(requireContext(),"CANCEL_REGISTER",getString(R.string.noti),getString(R.string.cancel_success));

                        // user notification
                        String desc_noti = getString(R.string.noti_cancel)+" "+room;
                        String time_noti = GetTimes.getTimeUpdate(requireContext());
                        NotifiClass notifiClass = new NotifiClass(desc_noti, time_noti, 0);
                        referenceUser.child("notifications").child(time_noti).setValue(notifiClass);
                        classroomArrayList.clear();
                        roomAdapter.notifyDataSetChanged();

                        // Refresh data
                        databaseProcess();
                        listener.onUpdateSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onUpdateFailure(e.getMessage());
                    }
                });
            }
        });
    }

    // Cancel register room
    private void cancelRoom(int position) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancel_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtDescDialog = dialog.findViewById(R.id.txtDescDialog);
        Button btnDialog = dialog.findViewById(R.id.btnDialog);
        Button btnDone = dialog.findViewById(R.id.btnDone);

        txtDescDialog.setText(getText(R.string.cancel_desc));

        // Confirm cancel
        btnDialog.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setCancelable(false);
            builder.setView(R.layout.loading_layout);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            // remove classroom from database
            removeRoom(position, new DatabaseUpdateListener() {
                @Override
                public void onUpdateSuccess() {
                    alertDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onUpdateFailure(String errorMessage) {
                    alertDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Skip
        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    // Click to item recycle view
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), RoomInfoActivity.class);
        intent.putExtra("room", classroomArrayList.get(position).getRoomName());
        intent.putExtra("date_study", classroomArrayList.get(position).getDate_study());
        intent.putExtra("teacher", classroomArrayList.get(position).getTeacher());
        intent.putExtra("subject", classroomArrayList.get(position).getSubject());
        intent.putExtra("time_study", classroomArrayList.get(position).getTime_study());
        intent.putExtra("time_signup", classroomArrayList.get(position).getTime_signup());
        intent.putExtra("floor", classroomArrayList.get(position).getFloorName());
        intent.putExtra("id", id);
        startActivity(intent);
    }

    // Long click to item recycle view
    @Override
    public void onItemLongClick(int position) {
        cancelRoom(position);
    }
}