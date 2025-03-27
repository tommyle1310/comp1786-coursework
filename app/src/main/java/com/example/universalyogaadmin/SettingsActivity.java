package com.example.universalyogaadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private TextView resetDatabaseTextView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search); // Tạm thời dùng layout của SearchActivity

        resetDatabaseTextView = findViewById(R.id.resetDatabaseTextView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Nút Reset Database
        resetDatabaseTextView.setOnClickListener(v -> {
            // TODO: Xử lý reset database ở phần b
        });

        // Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
                return true;
            }  else if (itemId == R.id.nav_settings) {
                return true;
            }
            return false;
        });

        // Đặt item được chọn là Settings
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
    }
}