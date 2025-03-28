package com.example.universalyogaadmin;

import android.app.DatePickerDialog;
import android.os.Bundle;
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

public class EditClassInstanceActivity extends AppCompatActivity {

    private EditText dateEditText, commentsEditText;
    private Spinner teacherSpinner;
    private Button updateInstanceButton;
    private DatabaseHelper dbHelper;
    private int instanceId, classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class_instance);

        dbHelper = new DatabaseHelper(this);

        dateEditText = findViewById(R.id.dateEditText);
        commentsEditText = findViewById(R.id.commentsEditText);
        teacherSpinner = findViewById(R.id.teacherSpinner);
        updateInstanceButton = findViewById(R.id.updateInstanceButton);

        // Lấy dữ liệu từ Intent
        instanceId = getIntent().getIntExtra("instanceId", -1);
        classId = getIntent().getIntExtra("classId", -1);
        String date = getIntent().getStringExtra("date");
        String teacherName = getIntent().getStringExtra("teacherName");
        String comments = getIntent().getStringExtra("comments");

        if (instanceId == -1 || classId == -1) {
            Toast.makeText(this, "Invalid instance or class ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị dữ liệu hiện tại
        dateEditText.setText(date);
        commentsEditText.setText(comments);

        // Setup Spinner cho Teacher
        List<Teacher> teacherList = dbHelper.getAllTeachers();
        ArrayAdapter<Teacher> teacherAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                teacherList
        );
        teacherAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        teacherSpinner.setAdapter(teacherAdapter);

        // Chọn teacher hiện tại
        for (int i = 0; i < teacherList.size(); i++) {
            if (teacherList.get(i).getName().equals(teacherName)) {
                teacherSpinner.setSelection(i);
                break;
            }
        }

        // Setup DatePicker cho Date
        dateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateEditText.setText(sdf.format(selectedDate.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        updateInstanceButton.setOnClickListener(v -> {
            String newDate = dateEditText.getText().toString().trim();
            Teacher selectedTeacher = (Teacher) teacherSpinner.getSelectedItem();
            String newTeacherName = selectedTeacher != null ? selectedTeacher.getName() : "";
            String newComments = commentsEditText.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (newDate.isEmpty() || newTeacherName.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra ngày có khớp với Day of the week của class không
            YogaClass yogaClass = getYogaClassById(classId);
            if (!isDateMatchingDayOfWeek(newDate, yogaClass.getDayOfWeek())) {
                Toast.makeText(this, "Selected date does not match the class's day of the week", Toast.LENGTH_LONG).show();
                return;
            }

            // Cập nhật ClassInstance
            ClassInstance instance = new ClassInstance(classId, newDate, newTeacherName, newComments.isEmpty() ? null : newComments);
            instance.setInstanceId(instanceId);

            // Cập nhật vào database
            int rowsAffected = dbHelper.updateClassInstance(instance);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Instance updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update instance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private YogaClass getYogaClassById(int classId) {
        List<YogaClass> classList = dbHelper.getAllClasses();
        for (YogaClass yogaClass : classList) {
            if (yogaClass.getId() == classId) {
                return yogaClass;
            }
        }
        return null;
    }

    private boolean isDateMatchingDayOfWeek(String date, String dayOfWeek) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(date));
            int dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK);
            String selectedDayOfWeek = "";
            switch (dayOfWeekInt) {
                case Calendar.MONDAY:
                    selectedDayOfWeek = "Monday";
                    break;
                case Calendar.TUESDAY:
                    selectedDayOfWeek = "Tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    selectedDayOfWeek = "Wednesday";
                    break;
                case Calendar.THURSDAY:
                    selectedDayOfWeek = "Thursday";
                    break;
                case Calendar.FRIDAY:
                    selectedDayOfWeek = "Friday";
                    break;
                case Calendar.SATURDAY:
                    selectedDayOfWeek = "Saturday";
                    break;
                case Calendar.SUNDAY:
                    selectedDayOfWeek = "Sunday";
                    break;
            }
            return selectedDayOfWeek.equalsIgnoreCase(dayOfWeek);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}