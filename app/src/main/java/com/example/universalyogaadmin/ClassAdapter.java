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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        YogaClass yogaClass = classList.get(position);

        // Gán dữ liệu cho các view
        holder.classNameTextView.setText(yogaClass.getClassName());
        holder.priceTextView.setText("$" + yogaClass.getPrice());
        holder.capacityTextView.setText(yogaClass.getCapacity() + " people");
        holder.timeTextView.setText(yogaClass.getClassTime());

        // Hiển thị hình ảnh nếu có
        if (yogaClass.getImageUri() != null) {
            holder.classImageView.setImageURI(yogaClass.getImageUri());
        }

        // Sự kiện click vào item
        holder.itemView.setOnClickListener(v -> listener.onClassClick(yogaClass));

        // Sự kiện click vào icon Edit
        holder.editIcon.setOnClickListener(v -> listener.onEditClick(yogaClass));

        // Sự kiện click vào icon Delete
        holder.deleteIcon.setOnClickListener(v -> listener.onDeleteClick(yogaClass));
    }

    @Override
    public int getItemCount() {
        return classList != null ? classList.size() : 0;
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView classNameTextView, priceTextView, capacityTextView, timeTextView;
        ImageView classImageView, editIcon, deleteIcon;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            capacityTextView = itemView.findViewById(R.id.capacityTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            classImageView = itemView.findViewById(R.id.classImageView);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}