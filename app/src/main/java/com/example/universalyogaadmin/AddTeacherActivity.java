package com.example.universalyogaadmin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AddTeacherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        // Hiển thị AddTeacherFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddTeacherFragment())
                    .commit();
        }
    }
}