package com.example.universalyogaadmin;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

public class AddClassFragment extends Fragment {

    private EditText classNameEditText, classTypeEditText, timeEditText, capacityEditText, priceEditText, descriptionEditText;
    private Spinner teacherSpinner, durationSpinner;
    private Button addClassButton, selectImageButton;
    private ImageView courseImageView;
    private Uri imageUri;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    courseImageView.setImageURI(uri);
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_class, container, false);

        // Ánh xạ các view
        courseImageView = view.findViewById(R.id.courseImageView);
        selectImageButton = view.findViewById(R.id.selectImageButton);
        classNameEditText = view.findViewById(R.id.classNameEditText);
        classTypeEditText = view.findViewById(R.id.classTypeEditText);
        teacherSpinner = view.findViewById(R.id.teacherSpinner);
        timeEditText = view.findViewById(R.id.timeEditText);
        capacityEditText = view.findViewById(R.id.capacityEditText);
        durationSpinner = view.findViewById(R.id.durationSpinner);
        priceEditText = view.findViewById(R.id.priceEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        addClassButton = view.findViewById(R.id.addClassButton);

        // Xử lý chọn hình ảnh
        selectImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Setup Spinner cho Teacher
        ArrayAdapter<CharSequence> teacherAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.teachers_array,
                android.R.layout.simple_spinner_item
        );
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(teacherAdapter);

        // Setup Spinner cho Duration
        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.duration_array,
                android.R.layout.simple_spinner_item
        );
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(durationAdapter);

        // Xử lý nút Add Class
        addClassButton.setOnClickListener(v -> {
            // TODO: Xử lý lưu class vào database ở phần b
            // Lưu imageUri cùng với các field khác
        });

        return view;
    }
}