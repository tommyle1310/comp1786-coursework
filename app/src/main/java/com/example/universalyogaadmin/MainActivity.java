package com.example.universalyogaadmin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton addClassFab;
    private ClassAdapter adapter;
    private List<YogaClass> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.classRecyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        addClassFab = findViewById(R.id.addClassFab);

        // Setup RecyclerView
        classList = getSampleClasses();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassAdapter(classList, new ClassAdapter.OnClassClickListener() {
            @Override
            public void onClassClick(YogaClass yogaClass) {
                Intent intent = new Intent(MainActivity.this, ClassDetailsActivity.class);
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
        recyclerView.setAdapter(adapter);

        // Xử lý FAB để mở AddActivity
        addClassFab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddActivity.class));
        });

        // Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                finish();
                return true;
            }
            return false;
        });

        // Đặt item được chọn là Home
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    // Dữ liệu mẫu
    private List<YogaClass> getSampleClasses() {
        List<YogaClass> classes = new ArrayList<>();
        classes.add(new YogaClass("Tammy Tao", "River flow", "Flow Yoga", "10:00AM - 11:00AM", 20, 30, "Mon, Tue", "60 minutes", "A relaxing flow yoga class"));
        classes.add(new YogaClass("John Doe", "River flow", "Aerial Yoga", "10:00AM - 11:00AM", 20, 30, "Mon, Tue", "60 minutes", "An aerial yoga class for beginners"));
        return classes;
    }
}

// Class mẫu cho YogaClass
class YogaClass {
    private String teacherName;
    private String className;
    private String classType;
    private String classTime;
    private int price;
    private int capacity;
    private String days;
    private String duration;
    private String description;

    public YogaClass(String teacherName, String className, String classType, String classTime, int price, int capacity, String days, String duration, String description) {
        this.teacherName = teacherName;
        this.className = className;
        this.classType = classType;
        this.classTime = classTime;
        this.price = price;
        this.capacity = capacity;
        this.days = days;
        this.duration = duration;
        this.description = description;
    }

    public String getTeacherName() { return teacherName; }
    public String getClassName() { return className; }
    public String getClassType() { return classType; }
    public String getClassTime() { return classTime; }
    public int getPrice() { return price; }
    public int getCapacity() { return capacity; }
    public String getDays() { return days; }
    public String getDuration() { return duration; }
    public String getDescription() { return description; }
}