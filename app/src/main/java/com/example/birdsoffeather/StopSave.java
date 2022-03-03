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
    private String currentSessionName;
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getAvailableCourses());
        dropdown = findViewById(R.id.class_dropdown);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(listener);

        customNameEditText = findViewById(R.id.rename_other_edit_text);

        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        currentSessionName = preferences.getString("currentSession", "");

        TextView renamedSessionTextView = findViewById(R.id.renamed_session_text_view);
        renamedSessionTextView.setText(currentSessionName);

    }

    /**
     * gets the courses from current quarter of the user based on year and quarter input
     * @param year current year
     * @param quarter int representation of current quarter. SSI, SS2, and SSS are all part of the "Summer quarter"
     * @param userID user's ID
     * @return the list of the classes that user took current quarter
     */
    public List<Course> getCurrCourses(int year, int quarter, String userID) {
        List<Course> courses = new ArrayList<>();
        switch(quarter) {
            case 0:
                courses.addAll(db.coursesDao().getCoursesForQuarter("" + year, "Winter", userID));
                break;
            case 1:
                courses.addAll(db.coursesDao().getCoursesForQuarter("" + year, "Spring", userID));
                break;
            case 2:
                courses.addAll(db.coursesDao().getCoursesForQuarter("" + year, "Summer Session I", userID));
                courses.addAll(db.coursesDao().getCoursesForQuarter("" + year, "Summer Session II", userID));
                courses.addAll(db.coursesDao().getCoursesForQuarter("" + year, "Special Summer Session", userID));
                break;
            case 3:
                courses.addAll(db.coursesDao().getCoursesForQuarter("" + year, "Fall", userID));
                break;
        }
        return courses;
    }

    /**
     * @return the courses from current quarter that has not been used as the session name
     */
    public List<String> getAvailableCourses() {
        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        String userID = preferences.getString("userID", null);

        int[] currQuarter = Utilities.getCurrentQuarterAndYear();

        List<Course> courses = getCurrCourses(currQuarter[1], currQuarter[0], userID);
        System.out.println(courses);
        List<String> courseStrings = new ArrayList<>();

        for (Course c: courses) {
            String courseString = c.subject + " " + c.number;
            if(isValidSessionName(courseString)) courseStrings.add(courseString);
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

            //remove the session name from shared preferences
            SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("currentSession", null);
            editor.apply();

            finish();
        } else {
            Utilities.showAlert(this, "This name is invalid or already in use");
        }

    }

    // If person uses back button to exit activity,we just don't bother changing its name
    private void renameSession(String newName) {
        db.sessionsDao().renameSession(currentSessionName, newName);
    }

    // check if name is a valid type and whether it is in use
    private boolean isValidSessionName(@NonNull String newName) {
        return db.sessionsDao().getPeopleForSession(newName).size() == 0 && !newName.equals("");
    }
}