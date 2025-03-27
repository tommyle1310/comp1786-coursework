package com.example.universalyogaadmin;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ClassDetailsActivity extends AppCompatActivity {

    private ImageView classImageView;
    private TextView teacherNameTextView, classNameTextView, classTypeTextView, classTimeTextView, priceTextView, capacityTextView, durationTextView, descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        // Ánh xạ các view
        classImageView = findViewById(R.id.classImageView);
        teacherNameTextView = findViewById(R.id.teacherNameTextView);
        classNameTextView = findViewById(R.id.classNameTextView);
        classTypeTextView = findViewById(R.id.classTypeTextView);
        classTimeTextView = findViewById(R.id.classTimeTextView);
        priceTextView = findViewById(R.id.priceTextView);
        capacityTextView = findViewById(R.id.capacityTextView);
        durationTextView = findViewById(R.id.durationTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        // Lấy dữ liệu từ Intent
        String teacherName = getIntent().getStringExtra("teacherName");
        String className = getIntent().getStringExtra("className");
        String classType = getIntent().getStringExtra("classType");
        String classTime = getIntent().getStringExtra("classTime");
        int price = getIntent().getIntExtra("price", 0);
        int capacity = getIntent().getIntExtra("capacity", 0);
        String duration = getIntent().getStringExtra("duration");
        String description = getIntent().getStringExtra("description");
        String imageUriString = getIntent().getStringExtra("imageUri");

        // Hiển thị dữ liệu
        teacherNameTextView.setText(teacherName);
        classNameTextView.setText(className);
        classTypeTextView.setText(classType);
        classTimeTextView.setText(classTime);
        priceTextView.setText("$" + price);
        capacityTextView.setText(capacity + " people");
        durationTextView.setText(duration);
        descriptionTextView.setText(description);

        // Hiển thị hình ảnh
        if (imageUriString != null) {
            classImageView.setImageURI(Uri.parse(imageUriString));
        }
    }
}