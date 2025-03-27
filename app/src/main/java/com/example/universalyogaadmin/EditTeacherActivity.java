package com.example.universalyogaadmin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditTeacherActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText;
    private Button updateTeacherButton;
    private DatabaseHelper dbHelper;
    private Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ các view
        nameEditText = findViewById(R.id.teacherNameEditText);
        emailEditText = findViewById(R.id.teacherEmailEditText);
        phoneEditText = findViewById(R.id.teacherPhoneEditText);
        updateTeacherButton = findViewById(R.id.updateTeacherButton);

        // Lấy dữ liệu từ Intent
        teacher = new Teacher(
                getIntent().getStringExtra("name"),
                getIntent().getStringExtra("email"),
                getIntent().getStringExtra("phone")
        );
        teacher.setId(getIntent().getIntExtra("id", -1));

        // Hiển thị dữ liệu lên các field
        nameEditText.setText(teacher.getName());
        emailEditText.setText(teacher.getEmail());
        phoneEditText.setText(teacher.getPhone());

        // Xử lý nút Update Teacher
        updateTeacherButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật đối tượng Teacher
            teacher.setName(name);
            teacher.setEmail(email);
            teacher.setPhone(phone);

            // Cập nhật vào database
            int rowsAffected = dbHelper.updateTeacher(teacher);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Teacher updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update teacher", Toast.LENGTH_SHORT).show();
            }
        });
    }
}