package com.example.universalyogaadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private EditText searchTeacherEditText, searchDateEditText;
    private Spinner dayOfWeekSpinner;
    private Button searchButton;
    private RecyclerView searchResultsRecyclerView;
    private BottomNavigationView bottomNavigationView;
    private ClassAdapter adapter;
    private List<YogaClass> classList;
    private List<YogaClass> filteredList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DatabaseHelper(this);

        searchTeacherEditText = findViewById(R.id.searchTeacherEditText);
        searchDateEditText = findViewById(R.id.searchDateEditText);
        dayOfWeekSpinner = findViewById(R.id.dayOfWeekSpinner);
        searchButton = findViewById(R.id.searchButton);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Setup Spinner cho Day of the Week
        ArrayAdapter<CharSequence> dayOfWeekAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.days_of_week,
                R.layout.spinner_item
        );
        dayOfWeekAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(dayOfWeekAdapter);

        // Setup DatePicker cho Date
        searchDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        searchDateEditText.setText(sdf.format(selectedDate.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Setup RecyclerView
        classList = dbHelper.getAllClasses();
        filteredList = new ArrayList<>(classList);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassAdapter(filteredList, new ClassAdapter.OnClassClickListener() {
            @Override
            public void onClassClick(YogaClass yogaClass) {
                Intent intent = new Intent(SearchActivity.this, ClassDetailsActivity.class);
                intent.putExtra("teacherName", yogaClass.getTeacherName());
                intent.putExtra("className", yogaClass.getClassName());
                intent.putExtra("classType", yogaClass.getClassType());
                intent.putExtra("classTime", yogaClass.getClassTime());
                intent.putExtra("price", yogaClass.getPrice());
                intent.putExtra("capacity", yogaClass.getCapacity());
                intent.putExtra("duration", yogaClass.getDuration());
                intent.putExtra("description", yogaClass.getDescription());
                intent.putExtra("dayOfWeek", yogaClass.getDayOfWeek());
                String imageUriString = yogaClass.getImageUri() != null ? yogaClass.getImageUri().toString() : null;
                intent.putExtra("imageUri", imageUriString);
                startActivity(intent);
            }

            @Override
            public void onEditClick(YogaClass yogaClass) {
                // Không cần edit trong màn hình search
            }

            @Override
            public void onDeleteClick(YogaClass yogaClass) {
                // Không cần delete trong màn hình search
            }

            @Override
            public void onViewInstancesClick(YogaClass yogaClass) {
                Intent intent = new Intent(SearchActivity.this, ClassInstancesActivity.class);
                intent.putExtra("classId", yogaClass.getId());
                startActivity(intent);
            }
        });
        searchResultsRecyclerView.setAdapter(adapter);

        // Tìm kiếm theo teacher name (real-time)
        searchTeacherEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClasses();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Tìm kiếm khi nhấn nút Search
        searchButton.setOnClickListener(v -> filterClasses());

        // Setup Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_classes) {
                startActivity(new Intent(SearchActivity.this, ListClassActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teachers) {
                startActivity(new Intent(SearchActivity.this, ListTeacherActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
    }

    private void filterClasses() {
        String teacherQuery = searchTeacherEditText.getText().toString().trim().toLowerCase();
        String dayOfWeek = dayOfWeekSpinner.getSelectedItem() != null ? dayOfWeekSpinner.getSelectedItem().toString() : "";
        String dateQuery = searchDateEditText.getText().toString().trim();

        filteredList.clear();

        for (YogaClass yogaClass : classList) {
            boolean matchesTeacher = teacherQuery.isEmpty() || yogaClass.getTeacherName().toLowerCase().startsWith(teacherQuery);
            boolean matchesDayOfWeek = dayOfWeek.isEmpty() || yogaClass.getDayOfWeek().equalsIgnoreCase(dayOfWeek);
            boolean matchesDate = true;

            if (!dateQuery.isEmpty()) {
                matchesDate = false;
                List<ClassInstance> instances = dbHelper.getClassInstancesByClassId(yogaClass.getId());
                for (ClassInstance instance : instances) {
                    if (instance.getDate().equals(dateQuery)) {
                        matchesDate = true;
                        break;
                    }
                }
            }

            if (matchesTeacher && matchesDayOfWeek && matchesDate) {
                filteredList.add(yogaClass);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No classes found", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }
}