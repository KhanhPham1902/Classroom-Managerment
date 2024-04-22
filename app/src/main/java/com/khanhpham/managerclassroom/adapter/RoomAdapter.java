package com.khanhpham.managerclassroom.adapter;

import static androidx.core.content.res.TypedArrayUtils.getText;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.khanhpham.managerclassroom.R;
import com.khanhpham.managerclassroom.models.Classroom;
import com.khanhpham.managerclassroom.models.OnItemClickListener;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private ArrayList<Classroom> classroomArrayList;
    private Context context;
    private final OnItemClickListener listener;

    public RoomAdapter(ArrayList<Classroom> classroomArrayList, Context context, OnItemClickListener listener) {
        this.classroomArrayList = classroomArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.info_classroom, parent, false);
        return new RoomAdapter.ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.ViewHolder holder, int position) {
        holder.txtRoomInfo.setText(classroomArrayList.get(position).getRoomName());
        holder.txtDateInfo.setText(classroomArrayList.get(position).getDate_study());
        holder.txtSubjectInfo.setText(classroomArrayList.get(position).getSubject());
        holder.txtTimeInfo.setText(classroomArrayList.get(position).getTime_study());
        String time = classroomArrayList.get(position).getTime_study();
        switch (time){
            case "morning":
                holder.txtTimeInfo.setText(context.getText(R.string.morning_time));
                break;
            case "afternoon":
                holder.txtTimeInfo.setText(context.getText(R.string.afternoon_time));
                break;
            case "evening":
                holder.txtTimeInfo.setText(context.getText(R.string.evening_time));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return classroomArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRoomInfo, txtTimeInfo, txtDateInfo, txtSubjectInfo;
        CardView cardView;
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            txtRoomInfo = itemView.findViewById(R.id.txtRoomInfo);
            txtTimeInfo = itemView.findViewById(R.id.txtTimeInfo);
            txtSubjectInfo = itemView.findViewById(R.id.txtSubjectInfo);
            txtDateInfo = itemView.findViewById(R.id.txtDateStudyInfo);
            cardView = itemView.findViewById(R.id.cvRecycleView);

            // Click
            itemView.setOnClickListener(v -> {
                if(listener!=null){
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
            // Long click
            itemView.setOnLongClickListener(v -> {
                if(listener!=null){
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onItemLongClick(position);
                        return true;
                    }
                }
                return false;
            });
        }
    }
}
