package com.example.birdsoffeather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.PersonWithCourses;

import java.util.ArrayList;
import java.util.List;

public class StopSave extends AppCompatActivity {

    private static final String CHOOSE_OTHER_STRING = "Other Name";

    private Spinner dropdown;
    private String currentSelectedName;
    private boolean usingCustomName = false;
    AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            String s = (String) adapterView.getItemAtPosition(pos);
            if (s.equals(CHOOSE_OTHER_STRING)) {
                customNameEditText.setVisibility(View.VISIBLE);
                usingCustomName = true;
            } else {
                customNameEditText.setVisibility(View.INVISIBLE);
                currentSelectedName = s;
                usingCustomName = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            customNameEditText.setVisibility(View.INVISIBLE);
            usingCustomName = false;
        }
    };

    EditText customNameEditText;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_save);

        db = AppDatabase.singleton(getApplicationContext());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getUserCourses());
        dropdown = findViewById(R.id.class_dropdown);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(listener);

        customNameEditText = findViewById(R.id.rename_other_edit_text);

        TextView renamedSessionTextView = findViewById(R.id.renamed_session_text_view);
        renamedSessionTextView.setText(getIntent().getStringExtra("session_name"));

    }

    private List<String> getUserCourses() {
        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        String userID = preferences.getString("userID", null);

        List<Course> courses = db.coursesDao().getForPerson(userID);
        System.out.println(courses);
        List<String> courseStrings = new ArrayList<>();

        for (Course c: courses) {
            courseStrings.add(c.subject + " " + c.number);
        }
        courseStrings.add(CHOOSE_OTHER_STRING);

        return courseStrings;
    }

    public void onConfirmName(View view) {
        String newName = currentSelectedName;
        if (usingCustomName) {
            newName = customNameEditText.getText().toString();
        }

        if (newName == null) {
            Utilities.showAlert(this, "Please select a session name");
        } else if (isValidSessionName(newName)) {
            renameSession(newName);
            finish();
        } else {
            Utilities.showAlert(this, "This name is invalid or already in use");
        }

    }

    // If person uses back button to exit activity,we just dont bother changing its name
    //TODO rename the session in the db
    private void renameSession(String newName) {
    }

    // TODO check if name is a valid type and whether it is in use
    private boolean isValidSessionName(@NonNull String newName) {


        return true;
    }
}