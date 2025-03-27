package com.example.universalyogaadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private EditText classNameEditText, classTypeEditText, classTimeEditText,
            priceEditText, capacityEditText, durationEditText, descriptionEditText;
    private Spinner teacherSpinner;
    private ImageView classImageView;
    private Button saveButton, selectClassImageButton;
    private DatabaseHelper dbHelper;
    private Uri classImageUri;
    private static final int PICK_CLASS_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DatabaseHelper(this);

        classNameEditText = findViewById(R.id.classNameEditText);
        teacherSpinner = findViewById(R.id.teacherSpinner);
        classTypeEditText = findViewById(R.id.classTypeEditText);
        classTimeEditText = findViewById(R.id.classTimeEditText);
        priceEditText = findViewById(R.id.priceEditText);
        capacityEditText = findViewById(R.id.capacityEditText);
        durationEditText = findViewById(R.id.durationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        classImageView = findViewById(R.id.classImageView);
        saveButton = findViewById(R.id.saveButton);
        selectClassImageButton = findViewById(R.id.selectClassImageButton);

        // Fetch danh sách giáo viên và hiển thị trong Spinner
        List<Teacher> teacherList = dbHelper.getAllTeachers();
        ArrayAdapter<Teacher> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherList);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(teacherAdapter);

        // Log để kiểm tra danh sách giáo viên
        Log.d("AddActivity", "Teacher list size: " + teacherList.size());
        for (Teacher teacher : teacherList) {
            Log.d("AddActivity", "Teacher: " + teacher.getName());
        }

        selectClassImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_CLASS_IMAGE_REQUEST);
        });

        saveButton.setOnClickListener(v -> {
            String className = classNameEditText.getText().toString();
            Teacher selectedTeacher = (Teacher) teacherSpinner.getSelectedItem();
            String classType = classTypeEditText.getText().toString();
            String classTime = classTimeEditText.getText().toString();
            String priceStr = priceEditText.getText().toString();
            String capacityStr = capacityEditText.getText().toString();
            String duration = durationEditText.getText().toString();
            String description = descriptionEditText.getText().toString();

            if (className.isEmpty() || selectedTeacher == null || classType.isEmpty() || classTime.isEmpty() ||
                    priceStr.isEmpty() || capacityStr.isEmpty() || duration.isEmpty() || description.isEmpty()) {
                android.widget.Toast.makeText(this, "Please fill all fields", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            int price = Integer.parseInt(priceStr);
            int capacity = Integer.parseInt(capacityStr);

            YogaClass yogaClass = new YogaClass(selectedTeacher.getName(), className, classType, classTime, price, capacity, duration, description);
            yogaClass.setImageUri(classImageUri);
            dbHelper.addClass(yogaClass);

            Log.d("AddActivity", "Saved class with imageUri: " + classImageUri);

            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_CLASS_IMAGE_REQUEST) {
                classImageUri = data.getData();
                classImageView.setImageURI(classImageUri);
                Log.d("AddActivity", "Selected class image URI: " + classImageUri);
            }
        }
    }
}