package com.example.universalyogaadmin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.Log;
import java.util.List;

public class ListTeacherActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton addTeacherFab;
    private TeacherAdapter adapter;
    private List<Teacher> teacherList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_teacher);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.teacherRecyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        addTeacherFab = findViewById(R.id.addTeacherFab);

        // Setup RecyclerView
        teacherList = dbHelper.getAllTeachers();
        Log.d("ListTeacherActivity", "onCreate: teacherList size = " + teacherList.size());
        for (Teacher teacher : teacherList) {
            Log.d("ListTeacherActivity", "Teacher: " + teacher.getId() + ", " + teacher.getName());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeacherAdapter(teacherList, new TeacherAdapter.OnTeacherClickListener() {
            @Override
            public void onEditClick(Teacher teacher) {
                Intent intent = new Intent(ListTeacherActivity.this, EditTeacherActivity.class);
                intent.putExtra("id", teacher.getId());
                intent.putExtra("name", teacher.getName());
                intent.putExtra("email", teacher.getEmail());
                intent.putExtra("phone", teacher.getPhone());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Teacher teacher) {
                dbHelper.deleteTeacher(teacher.getId());
                teacherList.remove(teacher);
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);

        // Xử lý FAB để mở AddTeacherActivity
        addTeacherFab.setOnClickListener(v -> {
            startActivity(new Intent(ListTeacherActivity.this, AddTeacherActivity.class));
        });

        // Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(ListTeacherActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_classes) {
                startActivity(new Intent(ListTeacherActivity.this, ListClassActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teachers) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(ListTeacherActivity.this, SearchActivity.class));
                finish();
                return true;
            }
            return false;
        });

        // Đặt item được chọn là Teachers
        bottomNavigationView.setSelectedItemId(R.id.nav_teachers);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật danh sách khi quay lại từ EditTeacherActivity hoặc AddTeacherActivity
        teacherList.clear();
        teacherList.addAll(dbHelper.getAllTeachers());
        Log.d("ListTeacherActivity", "onResume: teacherList size = " + teacherList.size());
        for (Teacher teacher : teacherList) {
            Log.d("ListTeacherActivity", "Teacher: " + teacher.getId() + ", " + teacher.getName());
        }
        adapter.notifyDataSetChanged();
    }
}