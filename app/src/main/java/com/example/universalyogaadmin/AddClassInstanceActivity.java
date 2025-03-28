package com.example.universalyogaadmin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddClassInstanceActivity extends AppCompatActivity {

    private static final String TAG = "AddClassInstanceActivity";
    private EditText dateEditText, commentsEditText;
    private Spinner teacherSpinner;
    private Button addInstanceButton;
    private DatabaseHelper dbHelper;
    private int classId;
    private String classDayOfWeek;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class_instance);

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();

        dateEditText = findViewById(R.id.dateEditText);
        teacherSpinner = findViewById(R.id.teacherSpinner);
        commentsEditText = findViewById(R.id.commentsEditText);
        addInstanceButton = findViewById(R.id.addInstanceButton);

        // Lấy classId từ Intent
        classId = getIntent().getIntExtra("classId", -1);
        if (classId == -1) {
            Toast.makeText(this, "Invalid class ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy thông tin lớp học để lấy "Day of Week"
        List<YogaClass> classList = dbHelper.getAllClasses();
        for (YogaClass yogaClass : classList) {
            if (yogaClass.getId() == classId) {
                classDayOfWeek = yogaClass.getDayOfWeek();
                break;
            }
        }

        // Setup Spinner cho Teacher
        List<Teacher> teacherList = dbHelper.getAllTeachers();
        ArrayAdapter<Teacher> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherList);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(teacherAdapter);

        // Thêm DatePickerDialog khi nhấn vào dateEditText
        dateEditText.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddClassInstanceActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateEditText.setText(sdf.format(calendar.getTime()));
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        addInstanceButton.setOnClickListener(v -> {
            String date = dateEditText.getText().toString();
            Teacher selectedTeacher = (Teacher) teacherSpinner.getSelectedItem();
            String comments = commentsEditText.getText().toString();

            if (date.isEmpty() || selectedTeacher == null) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem ngày có khớp với "Day of Week" của lớp học không
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Calendar tempCalendar = Calendar.getInstance();
                tempCalendar.setTime(sdf.parse(date));
                String selectedDayOfWeek = new SimpleDateFormat("EEEE", Locale.US).format(tempCalendar.getTime()); // Dùng Locale.US để chuẩn hóa

                // Chuẩn hóa trước khi so sánh
                String normalizedSelectedDay = selectedDayOfWeek.trim().toLowerCase();
                String normalizedClassDay = classDayOfWeek.trim().toLowerCase();

                Log.d(TAG, "Selected Day: " + normalizedSelectedDay);
                Log.d(TAG, "Class Day: " + normalizedClassDay);

                if (!normalizedSelectedDay.equals(normalizedClassDay)) {
                    Toast.makeText(this, "Selected date does not match the class day of the week (" + classDayOfWeek + ")", Toast.LENGTH_LONG).show();
                    return;
                }

                ClassInstance classInstance = new ClassInstance(classId, date, selectedTeacher.getName(), comments);
                long instanceId = dbHelper.addClassInstance(classInstance);

                if (instanceId != -1) {
                    Toast.makeText(this, "Class instance added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add class instance", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Invalid date format. Use dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}