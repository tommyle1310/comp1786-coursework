package com.example.universalyogaadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private List<YogaClass> classList;
    private OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(YogaClass yogaClass);
        void onEditClick(YogaClass yogaClass);
        void onDeleteClick(YogaClass yogaClass);
    }

    public ClassAdapter(List<YogaClass> classList, OnClassClickListener listener) {
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        YogaClass yogaClass = classList.get(position);
        holder.classNameTextView.setText(yogaClass.getClassName());
        holder.classPriceTextView.setText("$" + yogaClass.getPrice());
        holder.classCapacityTextView.setText(yogaClass.getCapacity() + " people");
        holder.classTimeTextView.setText(yogaClass.getClassTime());
        holder.classDaysTextView.setText("[" + yogaClass.getDays() + "]");

        // Nhấn vào toàn bộ card để mở ClassDetailsActivity
        holder.itemView.setOnClickListener(v -> listener.onClassClick(yogaClass));

        // Nhấn vào icon Edit
        holder.editIcon.setOnClickListener(v -> listener.onEditClick(yogaClass));

        // Nhấn vào icon Delete
        holder.deleteIcon.setOnClickListener(v -> listener.onDeleteClick(yogaClass));
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        ImageView teacherAvatar, editIcon, deleteIcon;
        TextView classNameTextView, classPriceTextView, classCapacityTextView, classTimeTextView, classDaysTextView;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherAvatar = itemView.findViewById(R.id.teacherAvatar);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            classPriceTextView = itemView.findViewById(R.id.classPriceTextView);
            classCapacityTextView = itemView.findViewById(R.id.classCapacityTextView);
            classTimeTextView = itemView.findViewById(R.id.classTimeTextView);
            classDaysTextView = itemView.findViewById(R.id.classDaysTextView);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}