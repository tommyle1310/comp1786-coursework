package com.example.universalyogaadmin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.util.List;

public class ListClassActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton addClassFab;
    private ClassAdapter adapter;
    private List<YogaClass> classList;
    private DatabaseHelper dbHelper;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_class);

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.classRecyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        addClassFab = findViewById(R.id.addClassFab);

        if (checkPermission()) {
            setupRecyclerView();
        } else {
            requestPermission();
        }

        addClassFab.setOnClickListener(v -> {
            startActivity(new Intent(ListClassActivity.this, AddActivity.class));
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(ListClassActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_classes) {
                return true;
            } else if (itemId == R.id.nav_teachers) {
                startActivity(new Intent(ListClassActivity.this, ListTeacherActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(ListClassActivity.this, SearchActivity.class));
                finish();
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_classes);
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
        Log.d("ListClassActivity", "onCreate: classList size = " + classList.size());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassAdapter(classList, new ClassAdapter.OnClassClickListener() {
            @Override
            public void onClassClick(YogaClass yogaClass) {
                Intent intent = new Intent(ListClassActivity.this, ClassDetailsActivity.class);
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
                Intent intent = new Intent(ListClassActivity.this, EditClassActivity.class);
                intent.putExtra("id", yogaClass.getId());
                intent.putExtra("teacherName", yogaClass.getTeacherName());
                intent.putExtra("className", yogaClass.getClassName());
                intent.putExtra("classType", yogaClass.getClassType());
                intent.putExtra("classTime", yogaClass.getClassTime());
                intent.putExtra("dayOfWeek", yogaClass.getDayOfWeek());
                intent.putExtra("price", yogaClass.getPrice());
                intent.putExtra("capacity", yogaClass.getCapacity());
                intent.putExtra("duration", yogaClass.getDuration());
                intent.putExtra("description", yogaClass.getDescription());
                String imageUriString = yogaClass.getImageUri() != null ? yogaClass.getImageUri().toString() : null;
                intent.putExtra("imageUri", imageUriString);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(YogaClass yogaClass) {
                dbHelper.deleteClass(yogaClass.getId());
                classList.remove(yogaClass);
                adapter.notifyDataSetChanged();
                Toast.makeText(ListClassActivity.this, "Class deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onViewInstancesClick(YogaClass yogaClass) {
                Intent intent = new Intent(ListClassActivity.this, ClassInstancesActivity.class);
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
        Log.d("ListClassActivity", "onResume: classList size = " + classList.size());
        adapter.notifyDataSetChanged();
    }
}