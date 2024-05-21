package com.khanhpham.managerclassroom.fragments;

import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khanhpham.managerclassroom.R;
import com.khanhpham.managerclassroom.adapter.NotificationAdapter;
import com.khanhpham.managerclassroom.models.DatabaseUpdateListener;
import com.khanhpham.managerclassroom.models.NotifiClass;
import com.khanhpham.managerclassroom.models.OnItemClickListener;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

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

        // Swipe left to delete item notification
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvNotification);

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
                        notifiClassArrayList.add(0,notifiClass);
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

    // Remove notification from database
    private void deleteAllNoti(DatabaseUpdateListener listener){
        DatabaseReference reference = database.getReference().child("users").child(id).child("notifications");
        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(requireContext(), getString(R.string.clear_all_success), Toast.LENGTH_SHORT).show();
                notifiClassArrayList.clear();
                adapter.notifyDataSetChanged();
                txtNoNoti.setVisibility(View.VISIBLE);

                listener.onUpdateSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), getText(R.string.error), Toast.LENGTH_SHORT).show();
                listener.onUpdateFailure(e.getMessage());
            }
        });
    }

    // Clear all notification
    private void clearAllNoti() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_noti_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnDialog = dialog.findViewById(R.id.btnDialogNoti);
        Button btnDone = dialog.findViewById(R.id.btnDoneNoti);

        btnDialog.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setCancelable(false);
            builder.setView(R.layout.loading_layout);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            deleteAllNoti(new DatabaseUpdateListener() {
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

    // Delete item notification when swipe left
    NotifiClass deleteItem;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            deleteItem = notifiClassArrayList.get(position);
            notifiClassArrayList.remove(position);
            adapter.notifyDataSetChanged();

            Snackbar.make(rvNotification, R.string.deleted_noti, Snackbar.LENGTH_SHORT).setAction("Hoàn tác", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifiClassArrayList.add(position, deleteItem);
                    adapter.notifyDataSetChanged();
                }
            }).show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.damaged))
                    .addSwipeLeftActionIcon(R.drawable.bin)
                    .addSwipeLeftLabel(getString(R.string.delete))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }
}