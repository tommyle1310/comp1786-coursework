package com.example.universalyogaadmin;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ClassDetailsActivity extends AppCompatActivity {

    private TextView teacherNameTextView, classNameTextView, classTypeTextView, classTimeTextView, capacityTextView, durationTextView, priceTextView, descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        // Ánh xạ các view
        classNameTextView = findViewById(R.id.detailClassNameTextView);
        classTypeTextView = findViewById(R.id.detailClassTypeTextView);
        teacherNameTextView = findViewById(R.id.detailTeacherNameTextView);
        classTimeTextView = findViewById(R.id.detailClassTimeTextView);
        capacityTextView = findViewById(R.id.detailCapacityTextView);
        durationTextView = findViewById(R.id.detailDurationTextView);
        priceTextView = findViewById(R.id.detailPriceTextView);
        descriptionTextView = findViewById(R.id.detailDescriptionTextView);

        // Lấy dữ liệu từ Intent
        String className = getIntent().getStringExtra("className");
        String classType = getIntent().getStringExtra("classType");
        String teacherName = getIntent().getStringExtra("teacherName");
        String classTime = getIntent().getStringExtra("classTime");
        int capacity = getIntent().getIntExtra("capacity", 0);
        String duration = getIntent().getStringExtra("duration");
        int price = getIntent().getIntExtra("price", 0);
        String description = getIntent().getStringExtra("description");

        // Hiển thị dữ liệu
        classNameTextView.setText(className);
        classTypeTextView.setText(classType);
        teacherNameTextView.setText(teacherName);
        classTimeTextView.setText(classTime);
        capacityTextView.setText(String.valueOf(capacity));
        durationTextView.setText(duration);
        priceTextView.setText("$" + price);
        descriptionTextView.setText(description);
    }
}