package com.example.universalyogaadmin;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private List<YogaClass> classList;
    private OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(YogaClass yogaClass);
        void onEditClick(YogaClass yogaClass);
        void onDeleteClick(YogaClass yogaClass);
        void onViewInstancesClick(YogaClass yogaClass);
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

        holder.classNameTextView.setText(yogaClass.getClassName());
        holder.priceTextView.setText("$" + yogaClass.getPrice());
        holder.capacityTextView.setText(yogaClass.getCapacity() + " people");
        holder.timeTextView.setText(yogaClass.getClassTime());

        // Sử dụng Glide để hiển thị hình ảnh
        Uri imageUri = yogaClass.getImageUri(); // Thay đổi từ String sang Uri
        if (imageUri != null) {
            // Kiểm tra xem imageUri có phải là URL (bắt đầu bằng http hoặc https) hay không
            String imageUriString = imageUri.toString();
            if (imageUriString.startsWith("http")) {
                // Nếu là URL, tải trực tiếp bằng Glide
                Glide.with(holder.itemView.getContext())
                        .load(imageUriString) // Dùng String cho URL
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_close_clear_cancel)
                        .into(holder.classImageView);
            } else {
                // Nếu là Uri cục bộ, tải trực tiếp bằng Glide
                Glide.with(holder.itemView.getContext())
                        .load(imageUri) // Dùng Uri trực tiếp
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_close_clear_cancel)
                        .into(holder.classImageView);
            }
        } else {
            // Nếu không có hình ảnh, hiển thị placeholder
            holder.classImageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> listener.onClassClick(yogaClass));
        holder.editIcon.setOnClickListener(v -> listener.onEditClick(yogaClass));
        holder.deleteIcon.setOnClickListener(v -> listener.onDeleteClick(yogaClass));
        holder.viewInstancesIcon.setOnClickListener(v -> listener.onViewInstancesClick(yogaClass));
    }

    @Override
    public int getItemCount() {
        return classList != null ? classList.size() : 0;
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView classNameTextView, priceTextView, capacityTextView, timeTextView;
        ImageView classImageView, editIcon, deleteIcon, viewInstancesIcon;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            capacityTextView = itemView.findViewById(R.id.capacityTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            classImageView = itemView.findViewById(R.id.classImageView);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            viewInstancesIcon = itemView.findViewById(R.id.viewInstancesIcon);
        }
    }
}