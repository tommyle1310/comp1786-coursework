package com.example.universalyogaadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        // Gán dữ liệu cho các view
        if (holder.teacherNameTextView != null) {
            holder.teacherNameTextView.setText(teacher.getName());
        }
        if (holder.teacherContactTextView != null) {
            holder.teacherContactTextView.setText(teacher.getEmail() + " - " + teacher.getPhone());
        }

        // Gán sự kiện click cho icon Edit
        if (holder.editIcon != null) {
            holder.editIcon.setOnClickListener(v -> listener.onEditClick(teacher));
        }

        // Gán sự kiện click cho icon Delete
        if (holder.deleteIcon != null) {
            holder.deleteIcon.setOnClickListener(v -> listener.onDeleteClick(teacher));
        }
    }

    @Override
    public int getItemCount() {
        return teacherList != null ? teacherList.size() : 0;
    }

    static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView teacherNameTextView, teacherContactTextView;
        ImageView teacherImageView, editIcon, deleteIcon;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherNameTextView = itemView.findViewById(R.id.teacherNameTextView);
            teacherContactTextView = itemView.findViewById(R.id.teacherContactTextView);
            teacherImageView = itemView.findViewById(R.id.teacherImageView);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}