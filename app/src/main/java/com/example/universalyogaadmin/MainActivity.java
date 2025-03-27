package com.example.universalyogaadmin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton addClassFab;
    private ClassAdapter adapter;
    private List<YogaClass> classList; // Khởi tạo ngay từ đầu
    private DatabaseHelper dbHelper;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Khởi tạo classList ngay từ đầu
        classList = new ArrayList<>();

        recyclerView = findViewById(R.id.classRecyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        addClassFab = findViewById(R.id.addClassFab);

        // Kiểm tra và yêu cầu quyền
        if (checkPermission()) {
            setupRecyclerView();
        } else {
            requestPermission();
        }

        // Xử lý FAB để mở AddActivity
        addClassFab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddActivity.class));
        });

        // Bottom Navigation
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

        // Đặt item được chọn là Home
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private boolean checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: Sử dụng READ_MEDIA_IMAGES
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 12 trở xuống: Sử dụng READ_EXTERNAL_STORAGE
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
                // Quyền bị từ chối, hiển thị thông báo
                Toast.makeText(this, "Storage permission is required to display class images", Toast.LENGTH_LONG).show();
                setupRecyclerView(); // Vẫn hiển thị danh sách, nhưng hình ảnh có thể không tải được
            }
        }
    }

    private void setupRecyclerView() {
        List<YogaClass> newClassList = dbHelper.getAllClasses();
        Log.d("MainActivity", "onCreate: classList size = " + newClassList.size());
        classList.clear();
        classList.addAll(newClassList);
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
                intent.putExtra("imageUri", yogaClass.getImageUri() != null ? yogaClass.getImageUri().toString() : null);
                startActivity(intent);
            }

            @Override
            public void onEditClick(YogaClass yogaClass) {
                // Không sử dụng trong MainActivity
            }

            @Override
            public void onDeleteClick(YogaClass yogaClass) {
                // Không sử dụng trong MainActivity
            }
        }); // Loại bỏ tham số "false" vì constructor mới không cần
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật danh sách khi quay lại từ EditClassActivity hoặc AddActivity
        if (classList != null && dbHelper != null) {
            List<YogaClass> newClassList = dbHelper.getAllClasses();
            classList.clear();
            classList.addAll(newClassList);
            Log.d("MainActivity", "onResume: classList size = " + classList.size());
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
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
    private Uri imageUri; // Ảnh lớp học
    private Uri teacherImageUri; // Ảnh đại diện giáo viên

    public YogaClass(String teacherName, String className, String classType, String classTime, int price, int capacity, String duration, String description) {
        this.teacherName = teacherName;
        this.className = className;
        this.classType = classType;
        this.classTime = classTime;
        this.price = price;
        this.capacity = capacity;
        this.duration = duration;
        this.description = description;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getTeacherImageUri() {
        return teacherImageUri;
    }

    public void setTeacherImageUri(Uri teacherImageUri) {
        this.teacherImageUri = teacherImageUri;
    }
}