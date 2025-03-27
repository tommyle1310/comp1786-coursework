package com.example.universalyogaadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchActivity extends AppCompatActivity {

    private TextView resetDatabaseTextView;
    private BottomNavigationView bottomNavigationView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        resetDatabaseTextView = findViewById(R.id.resetDatabaseTextView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Nút Reset Database
        resetDatabaseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset database
                dbHelper.resetDatabase();
                // Thông báo reset thành công
                android.widget.Toast.makeText(SearchActivity.this, "Database reset successfully", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_classes) {
                startActivity(new Intent(SearchActivity.this, ListClassActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teachers) {
                startActivity(new Intent(SearchActivity.this, ListTeacherActivity.class));
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
}