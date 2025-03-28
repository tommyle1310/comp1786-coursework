package com.example.universalyogaadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ClassInstancesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView classTitleTextView;
    private FloatingActionButton addInstanceFab;
    private ClassInstanceAdapter adapter;
    private List<ClassInstance> instanceList;
    private DatabaseHelper dbHelper;
    private int classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_instances);

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.instanceRecyclerView);
        classTitleTextView = findViewById(R.id.classTitleTextView);
        addInstanceFab = findViewById(R.id.addInstanceFab);

        // Lấy classId từ Intent
        classId = getIntent().getIntExtra("classId", -1);
        if (classId == -1) {
            Toast.makeText(this, "Invalid class ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị tên yoga class
        YogaClass yogaClass = getYogaClassById(classId);
        if (yogaClass != null) {
            classTitleTextView.setText("Class Instances for: " + yogaClass.getClassName());
        } else {
            classTitleTextView.setText("Class Instances");
        }

        setupRecyclerView();

        addInstanceFab.setOnClickListener(v -> {
            Intent intent = new Intent(ClassInstancesActivity.this, AddClassInstanceActivity.class);
            intent.putExtra("classId", classId);
            startActivity(intent);
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

    private void setupRecyclerView() {
        instanceList = dbHelper.getClassInstancesByClassId(classId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassInstanceAdapter(instanceList, new ClassInstanceAdapter.OnInstanceClickListener() {
            @Override
            public void onEditClick(ClassInstance instance) {
                Intent intent = new Intent(ClassInstancesActivity.this, EditClassInstanceActivity.class);
                intent.putExtra("instanceId", instance.getInstanceId());
                intent.putExtra("classId", instance.getClassId());
                intent.putExtra("date", instance.getDate());
                intent.putExtra("teacherName", instance.getTeacherName());
                intent.putExtra("comments", instance.getComments());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(ClassInstance instance) {
                dbHelper.deleteClassInstance(instance.getInstanceId());
                instanceList.remove(instance);
                adapter.notifyDataSetChanged();
                Toast.makeText(ClassInstancesActivity.this, "Instance deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        instanceList.clear();
        instanceList.addAll(dbHelper.getClassInstancesByClassId(classId));
        adapter.notifyDataSetChanged();
    }
}