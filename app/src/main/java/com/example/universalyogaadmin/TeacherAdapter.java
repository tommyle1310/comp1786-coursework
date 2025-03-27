package com.example.universalyogaadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<Teacher> teacherList;
    private OnTeacherClickListener listener;

    public interface OnTeacherClickListener {
        void onEditClick(Teacher teacher);
        void onDeleteClick(Teacher teacher);
    }

    public TeacherAdapter(List<Teacher> teacherList, OnTeacherClickListener listener) {
        this.teacherList = teacherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teacherList.get(position);
        android.util.Log.d("TeacherAdapter", "onBindViewHolder: position = " + position + ", name = " + teacher.getName());
        if (holder.teacherNameTextView != null) {
            holder.teacherNameTextView.setText(teacher.getName());
        }
        if (holder.teacherEmailTextView != null) {
            holder.teacherEmailTextView.setText(teacher.getEmail());
        }
        if (holder.teacherPhoneTextView != null) {
            holder.teacherPhoneTextView.setText(teacher.getPhone());
        }

        if (holder.editButton != null) {
            holder.editButton.setOnClickListener(v -> listener.onEditClick(teacher));
        }
        if (holder.deleteButton != null) {
            holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(teacher));
        }
    }

    @Override
    public int getItemCount() {
        return teacherList != null ? teacherList.size() : 0;
    }

    static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView teacherNameTextView, teacherEmailTextView, teacherPhoneTextView;
        Button editButton, deleteButton;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherNameTextView = itemView.findViewById(R.id.teacherNameTextView);
            teacherEmailTextView = itemView.findViewById(R.id.teacherEmailTextView);
            teacherPhoneTextView = itemView.findViewById(R.id.teacherPhoneTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}