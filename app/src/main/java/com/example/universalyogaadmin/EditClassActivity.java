package com.example.universalyogaadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class EditClassActivity extends AppCompatActivity {

    private EditText classNameEditText, classTypeEditText, classTimeEditText, priceEditText, capacityEditText, durationEditText, descriptionEditText;
    private Spinner teacherSpinner;
    private Button updateClassButton, selectImageButton;
    private ImageView classImageView;
    private DatabaseHelper dbHelper;
    private Uri imageUri;
    private int classId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<Teacher> teacherList;
    private ArrayAdapter<String> teacherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ các view
        classNameEditText = findViewById(R.id.classNameEditText);
        classTypeEditText = findViewById(R.id.classTypeEditText);
        classTimeEditText = findViewById(R.id.classTimeEditText);
        priceEditText = findViewById(R.id.priceEditText);
        capacityEditText = findViewById(R.id.capacityEditText);
        durationEditText = findViewById(R.id.durationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        teacherSpinner = findViewById(R.id.teacherSpinner);
        updateClassButton = findViewById(R.id.updateClassButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        classImageView = findViewById(R.id.classImageView);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        classId = intent.getIntExtra("id", -1);
        String teacherName = intent.getStringExtra("teacherName");
        String className = intent.getStringExtra("className");
        String classType = intent.getStringExtra("classType");
        String classTime = intent.getStringExtra("classTime");
        int price = intent.getIntExtra("price", 0);
        int capacity = intent.getIntExtra("capacity", 0);
        String duration = intent.getStringExtra("duration");
        String description = intent.getStringExtra("description");
        String imageUriString = intent.getStringExtra("imageUri");

        // Hiển thị dữ liệu lên giao diện
        classNameEditText.setText(className);
        classTypeEditText.setText(classType);
        classTimeEditText.setText(classTime);
        priceEditText.setText(String.valueOf(price));
        capacityEditText.setText(String.valueOf(capacity));
        durationEditText.setText(duration);
        descriptionEditText.setText(description);
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            classImageView.setImageURI(imageUri);
        }

        // Lấy danh sách giáo viên và thiết lập spinner
        teacherList = dbHelper.getAllTeachers();
        List<String> teacherNames = new ArrayList<>();
        for (Teacher teacher : teacherList) {
            teacherNames.add(teacher.getName());
        }
        teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherNames);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(teacherAdapter);

        // Chọn giáo viên hiện tại trong spinner
        int teacherPosition = teacherNames.indexOf(teacherName);
        if (teacherPosition >= 0) {
            teacherSpinner.setSelection(teacherPosition);
        }

        // Xử lý chọn hình ảnh
        selectImageButton.setOnClickListener(v -> {
            Intent intentImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentImage, PICK_IMAGE_REQUEST);
        });

        // Xử lý nút Update Class
        updateClassButton.setOnClickListener(v -> {
            String updatedClassName = classNameEditText.getText().toString().trim();
            String updatedClassType = classTypeEditText.getText().toString().trim();
            String updatedClassTime = classTimeEditText.getText().toString().trim();
            String updatedPriceStr = priceEditText.getText().toString().trim();
            String updatedCapacityStr = capacityEditText.getText().toString().trim();
            String updatedDuration = durationEditText.getText().toString().trim();
            String updatedDescription = descriptionEditText.getText().toString().trim();
            String updatedTeacherName = teacherSpinner.getSelectedItem().toString();

            // Kiểm tra dữ liệu đầu vào
            if (updatedClassName.isEmpty() || updatedClassType.isEmpty() || updatedClassTime.isEmpty() || updatedPriceStr.isEmpty() || updatedCapacityStr.isEmpty() || updatedDuration.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(EditClassActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int updatedPrice, updatedCapacity;
            try {
                updatedPrice = Integer.parseInt(updatedPriceStr);
                updatedCapacity = Integer.parseInt(updatedCapacityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(EditClassActivity.this, "Price and Capacity must be numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng YogaClass
            YogaClass yogaClass = new YogaClass(updatedTeacherName, updatedClassName, updatedClassType, updatedClassTime, updatedPrice, updatedCapacity, updatedDuration, updatedDescription);
            yogaClass.setId(classId);
            if (imageUri != null) {
                yogaClass.setImageUri(imageUri);
            }

            // Cập nhật vào database
            int rowsAffected = dbHelper.updateClass(yogaClass);
            if (rowsAffected > 0) {
                Toast.makeText(EditClassActivity.this, "Class updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditClassActivity.this, "Failed to update class", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            classImageView.setImageURI(imageUri);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật danh sách giáo viên trong spinner
        teacherList.clear();
        teacherList.addAll(dbHelper.getAllTeachers());
        List<String> teacherNames = new ArrayList<>();
        for (Teacher teacher : teacherList) {
            teacherNames.add(teacher.getName());
        }
        teacherAdapter.clear();
        teacherAdapter.addAll(teacherNames);
        teacherAdapter.notifyDataSetChanged();
    }
}