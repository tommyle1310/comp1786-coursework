package com.example.universalyogaadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView searchRecyclerView;
    private TextView resetDatabaseTextView;
    private BottomNavigationView bottomNavigationView;
    private ClassAdapter adapter; // Sử dụng ClassAdapter thay vì SearchAdapter
    private List<YogaClass> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        resetDatabaseTextView = findViewById(R.id.resetDatabaseTextView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Setup RecyclerView
        classList = getSampleClasses();
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassAdapter(classList, new ClassAdapter.OnClassClickListener() {
            @Override
            public void onClassClick(YogaClass yogaClass) {
                Intent intent = new Intent(SearchActivity.this, ClassDetailsActivity.class);
                intent.putExtra("teacherName", yogaClass.getTeacherName());
                intent.putExtra("className", yogaClass.getClassName());
                intent.putExtra("classType", yogaClass.getClassType());
                intent.putExtra("classTime", yogaClass.getClassTime());
                intent.putExtra("price", yogaClass.getPrice());
                intent.putExtra("capacity", yogaClass.getCapacity());
                intent.putExtra("days", yogaClass.getDays());
                intent.putExtra("duration", yogaClass.getDuration());
                intent.putExtra("description", yogaClass.getDescription());
                startActivity(intent);
            }

            @Override
            public void onEditClick(YogaClass yogaClass) {
                // TODO: Mở màn hình chỉnh sửa class (phần b)
            }

            @Override
            public void onDeleteClick(YogaClass yogaClass) {
                // TODO: Xóa class khỏi database (phần b)
                classList.remove(yogaClass);
                adapter.notifyDataSetChanged();
            }
        });
        searchRecyclerView.setAdapter(adapter);

        // Nút Reset Database
        resetDatabaseTextView.setOnClickListener(v -> {
            // TODO: Xử lý reset database ở phần b
            classList.clear();
            adapter.notifyDataSetChanged();
        });

        // Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            }
            return false;
        });

        // Đặt item được chọn là Settings
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
    }

    // Dữ liệu mẫu
    private List<YogaClass> getSampleClasses() {
        List<YogaClass> classes = new ArrayList<>();
        classes.add(new YogaClass("Tammy Tao", "River flow", "Flow Yoga", "10:00AM - 11:00AM", 20, 30, "Mon, Tue", "60 minutes", "A relaxing flow yoga class"));
        classes.add(new YogaClass("John Doe", "River flow", "Aerial Yoga", "10:00AM - 11:00AM", 20, 30, "Mon, Tue", "60 minutes", "An aerial yoga class for beginners"));
        return classes;
    }
}