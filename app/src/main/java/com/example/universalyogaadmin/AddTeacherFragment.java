package com.example.universalyogaadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class AddTeacherFragment extends Fragment {

    private EditText teacherNameEditText, teacherEmailEditText, teacherPhoneEditText;
    private Button addTeacherButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_teacher, container, false);

        // Ánh xạ các view
        teacherNameEditText = view.findViewById(R.id.teacherNameEditText);
        teacherEmailEditText = view.findViewById(R.id.teacherEmailEditText);
        teacherPhoneEditText = view.findViewById(R.id.teacherPhoneEditText);
        addTeacherButton = view.findViewById(R.id.addTeacherButton);

        // Xử lý nút Add Teacher
        addTeacherButton.setOnClickListener(v -> {
            // TODO: Xử lý lưu teacher vào database ở phần b
        });

        return view;
    }
}