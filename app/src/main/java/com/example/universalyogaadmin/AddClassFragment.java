package com.example.universalyogaadmin;

import android.content.Context;
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
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class AddClassFragment extends Fragment {

    private EditText classNameEditText, classTypeEditText, timeEditText, capacityEditText, priceEditText, descriptionEditText;
    private Spinner teacherSpinner, durationSpinner;
    private Button addClassButton, selectImageButton;
    private ImageView courseImageView;
    private Uri imageUri;
    private File imageFile;
    private DatabaseHelper dbHelper;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    courseImageView.setImageURI(uri);
                    try {
                        imageFile = saveImageToInternalStorage(uri);
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_class, container, false);

        dbHelper = new DatabaseHelper(requireContext());

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

        selectImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Setup Spinner cho Teacher (dùng danh sách từ database)
        List<Teacher> teacherList = dbHelper.getAllTeachers();
        ArrayAdapter<Teacher> teacherAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                teacherList
        );
        teacherAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        teacherSpinner.setAdapter(teacherAdapter);

        // Setup Spinner cho Duration
        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.duration_array,
                R.layout.spinner_item
        );
        durationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        durationSpinner.setAdapter(durationAdapter);

        addClassButton.setOnClickListener(v -> {
            String className = classNameEditText.getText().toString().trim();
            String classType = classTypeEditText.getText().toString().trim();
            Teacher selectedTeacher = (Teacher) teacherSpinner.getSelectedItem();
            String teacherName = selectedTeacher != null ? selectedTeacher.getName() : "";
            String classTime = timeEditText.getText().toString().trim();
            String capacityStr = capacityEditText.getText().toString().trim();
            String priceStr = priceEditText.getText().toString().trim();
            String duration = durationSpinner.getSelectedItem().toString();
            String description = descriptionEditText.getText().toString().trim();

            if (className.isEmpty() || classType.isEmpty() || teacherName.isEmpty() || classTime.isEmpty() || capacityStr.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int capacity, price;
            try {
                capacity = Integer.parseInt(capacityStr);
                price = Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Capacity and Price must be numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            YogaClass yogaClass = new YogaClass(teacherName, className, classType, classTime, price, capacity, duration, description);
            if (imageFile != null) {
                yogaClass.setImageUri(Uri.fromFile(imageFile));
            }

            long id = dbHelper.addClass(yogaClass);
            if (id != -1) {
                Toast.makeText(requireContext(), "Class added successfully", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), "Failed to add class", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private File saveImageToInternalStorage(Uri uri) throws Exception {
        File directory = new File(requireContext().getFilesDir(), "images");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return file;
    }
}