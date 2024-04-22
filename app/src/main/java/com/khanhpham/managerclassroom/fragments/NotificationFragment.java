package com.khanhpham.managerclassroom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khanhpham.managerclassroom.R;
import com.khanhpham.managerclassroom.adapter.NotificationAdapter;
import com.khanhpham.managerclassroom.models.NotifiClass;
import com.khanhpham.managerclassroom.models.OnItemClickListener;

import java.util.ArrayList;

public class NotificationFragment extends Fragment implements OnItemClickListener {

    RecyclerView rvNotification;
    TextView txtClearAll, txtNoNoti;
    private ArrayList<NotifiClass> notifiClassArrayList;
    private NotificationAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        rvNotification = view.findViewById(R.id.rvNotification);
        txtClearAll = view.findViewById(R.id.txtClearAll);
        txtNoNoti = view.findViewById(R.id.txtNoNoti);

        // set up recycle view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvNotification.setLayoutManager(gridLayoutManager);

        notifiClassArrayList = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), notifiClassArrayList, this);
        rvNotification.setAdapter(adapter);

        if (getArguments() != null) {
            id = getArguments().getString("id");
        }

        // database process
        databaseProcess();

        // Clear all notifications
        txtClearAll.setOnClickListener(v -> {
            clearAllNoti();
        });

        return view;
    }

    // database process
    private void databaseProcess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout);
        AlertDialog dialog = builder.create();

        DatabaseReference reference = database.getReference().child("users").child(id).child("notifications");
        dialog.show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    txtNoNoti.setVisibility(View.GONE);
                    notifiClassArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        NotifiClass notifiClass = dataSnapshot.getValue(NotifiClass.class);
                        notifiClassArrayList.add(notifiClass);
                    }
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    txtNoNoti.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }

    // Clear all notification
    private void clearAllNoti() {
        DatabaseReference reference = database.getReference().child("users").child(id).child("notifications");
        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(requireContext(), getString(R.string.clear_all_success), Toast.LENGTH_SHORT).show();
                notifiClassArrayList.clear();
                adapter.notifyDataSetChanged();
                txtNoNoti.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), getText(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }
}