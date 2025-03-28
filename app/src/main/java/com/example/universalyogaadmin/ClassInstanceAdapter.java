package com.example.universalyogaadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClassInstanceAdapter extends RecyclerView.Adapter<ClassInstanceAdapter.InstanceViewHolder> {

    private List<ClassInstance> instanceList;
    private OnInstanceClickListener listener;

    public interface OnInstanceClickListener {
        void onEditClick(ClassInstance instance);
        void onDeleteClick(ClassInstance instance);
    }

    public ClassInstanceAdapter(List<ClassInstance> instanceList, OnInstanceClickListener listener) {
        this.instanceList = instanceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_instance, parent, false);
        return new InstanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstanceViewHolder holder, int position) {
        ClassInstance instance = instanceList.get(position);

        holder.dateTextView.setText(instance.getDate());
        holder.teacherTextView.setText(instance.getTeacherName());
        holder.commentsTextView.setText(instance.getComments() != null ? instance.getComments() : "No comments");

        holder.editIcon.setOnClickListener(v -> listener.onEditClick(instance));
        holder.deleteIcon.setOnClickListener(v -> listener.onDeleteClick(instance));
    }

    @Override
    public int getItemCount() {
        return instanceList != null ? instanceList.size() : 0;
    }

    static class InstanceViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, teacherTextView, commentsTextView;
        ImageView editIcon, deleteIcon;

        public InstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            teacherTextView = itemView.findViewById(R.id.teacherTextView);
            commentsTextView = itemView.findViewById(R.id.commentsTextView);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}