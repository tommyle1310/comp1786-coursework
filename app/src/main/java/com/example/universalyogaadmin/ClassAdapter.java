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
        void onEditClick(YogaClass yogaClass); // Không sử dụng trong MainActivity và ListClassActivity
        void onDeleteClick(YogaClass yogaClass); // Không sử dụng trong MainActivity và ListClassActivity
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
        // Hiển thị thông tin theo Figma
        if (holder.classNameTextView != null) {
            holder.classNameTextView.setText(yogaClass.getClassName());
        }
        if (holder.priceTextView != null) {
            holder.priceTextView.setText("$" + yogaClass.getPrice());
        }
        if (holder.capacityTextView != null) {
            holder.capacityTextView.setText(yogaClass.getCapacity() + " people");
        }
        if (holder.timeTextView != null) {
            holder.timeTextView.setText(yogaClass.getClassTime());
        }

        // Hiển thị ảnh của lớp học
        if (holder.classImageView != null) {
            if (yogaClass.getImageUri() != null) {
                android.util.Log.d("ClassAdapter", "Loading image URI: " + yogaClass.getImageUri());
                try {
                    holder.classImageView.setImageURI(yogaClass.getImageUri());
                } catch (SecurityException e) {
                    holder.classImageView.setImageResource(R.drawable.default_image);
                    android.util.Log.e("ClassAdapter", "Failed to load class image: " + yogaClass.getImageUri(), e);
                }
            } else {
                android.util.Log.d("ClassAdapter", "Image URI is null for class: " + yogaClass.getClassName());
                holder.classImageView.setImageResource(R.drawable.default_image);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onClassClick(yogaClass));
    }

    @Override
    public int getItemCount() {
        return classList != null ? classList.size() : 0;
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        ImageView classImageView;
        TextView classNameTextView, priceTextView, capacityTextView, timeTextView;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            classImageView = itemView.findViewById(R.id.classImageView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            capacityTextView = itemView.findViewById(R.id.capacityTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}