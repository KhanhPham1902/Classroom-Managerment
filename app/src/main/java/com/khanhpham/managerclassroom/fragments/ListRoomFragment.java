package com.khanhpham.managerclassroom.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khanhpham.managerclassroom.R;
import com.khanhpham.managerclassroom.adapter.RoomAdapter;
import com.khanhpham.managerclassroom.models.Classroom;
import com.khanhpham.managerclassroom.models.OnItemClickListener;

import java.util.ArrayList;

public class ListRoomFragment extends Fragment implements OnItemClickListener {
    private ArrayList<Classroom> classroomArrayList;
    private RoomAdapter roomAdapter;
    RecyclerView rvListRoom;
    TextView txtCheckRoomList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_room, container, false);

        rvListRoom = view.findViewById(R.id.recycleViewList);
        txtCheckRoomList = view.findViewById(R.id.txtCheckRoomList);

        // Set up recycle view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvListRoom.setLayoutManager(gridLayoutManager);

        classroomArrayList = new ArrayList<>();
        roomAdapter = new RoomAdapter(classroomArrayList, getContext(), this);
        rvListRoom.setAdapter(roomAdapter);

        // get data from MainActivity
        if(getArguments()!=null) {
            id = getArguments().getString("id");
        }

        // Database process
        databaseProcess();

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
                if(snapshot.exists()) {
                    txtCheckRoomList.setVisibility(View.GONE);
                    classroomArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Classroom classroom = dataSnapshot.getValue(Classroom.class);
                        classroomArrayList.add(classroom);
                    }
                    roomAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else{
                    txtCheckRoomList.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putInt("status",classroomArrayList.get(position).getStatus());
        bundle.putString("date_study",classroomArrayList.get(position).getDate_study());
        bundle.putString("time_study",classroomArrayList.get(position).getTime_study());
        bundle.putString("time_signup",classroomArrayList.get(position).getTime_signup());
        bundle.putString("floor",classroomArrayList.get(position).getFloorName());
        bundle.putString("room",classroomArrayList.get(position).getRoomName());
        ControlFragment controlFragment = new ControlFragment();
        controlFragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, controlFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemLongClick(int position) {
    }
}