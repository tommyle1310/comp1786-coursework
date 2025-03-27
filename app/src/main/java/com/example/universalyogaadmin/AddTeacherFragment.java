package com.example.universalyogaadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class AddTeacherFragment extends Fragment {

    private EditText teacherNameEditText, teacherEmailEditText, teacherPhoneEditText;
    private Button addTeacherButton;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_teacher, container, false);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Ánh xạ các view
        teacherNameEditText = view.findViewById(R.id.teacherNameEditText);
        teacherEmailEditText = view.findViewById(R.id.teacherEmailEditText);
        teacherPhoneEditText = view.findViewById(R.id.teacherPhoneEditText);
        addTeacherButton = view.findViewById(R.id.addTeacherButton);

        // Xử lý nút Add Teacher
        addTeacherButton.setOnClickListener(v -> {
            String name = teacherNameEditText.getText().toString().trim();
            String email = teacherEmailEditText.getText().toString().trim();
            String phone = teacherPhoneEditText.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng Teacher
            Teacher teacher = new Teacher(name, email, phone);

            // Lưu vào database
            try {
                long id = dbHelper.addTeacher(teacher);
                if (id > 0) { // Kiểm tra id hợp lệ
                    Toast.makeText(requireContext(), "Teacher added successfully", Toast.LENGTH_SHORT).show();
                    // Quay lại ListTeacherActivity để cập nhật danh sách
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireContext(), "Failed to add teacher", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error adding teacher: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}