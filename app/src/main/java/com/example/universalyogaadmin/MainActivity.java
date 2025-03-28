package com.example.universalyogaadmin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton addClassFab;
    private Button syncToCloudButton;
    private ClassAdapter adapter;
    private List<YogaClass> classList;
    private DatabaseHelper dbHelper;
    private CloudSyncManager cloudSyncManager;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        dbHelper = new DatabaseHelper(this);
        cloudSyncManager = new CloudSyncManager(this, dbHelper); // Truyền thêm Context

        classList = new ArrayList<>();

        recyclerView = findViewById(R.id.classRecyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        addClassFab = findViewById(R.id.addClassFab);
        syncToCloudButton = findViewById(R.id.syncToCloudButton);

        if (checkPermission()) {
            setupRecyclerView();
        } else {
            requestPermission();
        }

        addClassFab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddActivity.class));
        });

        syncToCloudButton.setOnClickListener(v -> cloudSyncManager.syncDataToCloud());

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_classes) {
                startActivity(new Intent(MainActivity.this, ListClassActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teachers) {
                startActivity(new Intent(MainActivity.this, ListTeacherActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                finish();
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private boolean checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupRecyclerView();
            } else {
                Toast.makeText(this, "Storage permission is required to display class images", Toast.LENGTH_LONG).show();
                setupRecyclerView();
            }
        }
    }

    private void setupRecyclerView() {
        classList = dbHelper.getAllClasses();
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
                intent.putExtra("duration", yogaClass.getDuration());
                intent.putExtra("description", yogaClass.getDescription());
                intent.putExtra("dayOfWeek", yogaClass.getDayOfWeek());
                String imageUriString = yogaClass.getImageUri() != null ? yogaClass.getImageUri().toString() : null;
                intent.putExtra("imageUri", imageUriString);
                startActivity(intent);
            }

            @Override
            public void onEditClick(YogaClass yogaClass) {
                // Không cần edit trong MainActivity
            }

            @Override
            public void onDeleteClick(YogaClass yogaClass) {
                // Không cần delete trong MainActivity
            }

            @Override
            public void onViewInstancesClick(YogaClass yogaClass) {
                Intent intent = new Intent(MainActivity.this, ClassInstancesActivity.class);
                intent.putExtra("classId", yogaClass.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        classList.clear();
        classList.addAll(dbHelper.getAllClasses());
        adapter.notifyDataSetChanged();
    }
}

// Class mẫu cho YogaClass
class YogaClass {
    private int id;
    private String teacherName;
    private String className;
    private String classType;
    private String classTime;
    private int price;
    private int capacity;
    private String duration;
    private String description;
    private String dayOfWeek;
    private Uri imageUri;
    private boolean isSynced;

    public YogaClass() {}

    public YogaClass(String teacherName, String className, String classType, String classTime, int price, int capacity, String duration, String description) {
        this.teacherName = teacherName;
        this.className = className;
        this.classType = classType;
        this.classTime = classTime;
        this.price = price;
        this.capacity = capacity;
        this.duration = duration;
        this.description = description;
        this.isSynced = false;
    }

    // Phương thức để chuyển đổi đối tượng thành Map phù hợp với Firebase
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("teacherName", teacherName);
        result.put("className", className);
        result.put("classType", classType);
        result.put("classTime", classTime);
        result.put("price", price);
        result.put("capacity", capacity);
        result.put("duration", duration);
        result.put("description", description);
        result.put("dayOfWeek", dayOfWeek);
        result.put("imageUri", imageUri != null ? imageUri.toString() : null); // Chuyển Uri thành String
        result.put("isSynced", isSynced);
        return result;
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }
    public String getClassTime() { return classTime; }
    public void setClassTime(String classTime) { this.classTime = classTime; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public Uri getImageUri() { return imageUri; }
    public void setImageUri(Uri imageUri) { this.imageUri = imageUri; }
    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }
}