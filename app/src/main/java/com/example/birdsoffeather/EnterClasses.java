package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EnterClasses extends AppCompatActivity{

    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    static int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_classes);

        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());

            db.coursesDao().deleteAll();
            db.personsWithCoursesDao().deleteAll();

            SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
            String name = preferences.getString("name", "No Name");
            String url = preferences.getString("Photo URL", "No URL");

            Person user = new Person(0, name, url);
            db.personsWithCoursesDao().insertPerson(user);
            Log.i("Database", "Added person: " + user.toString());

            return null;
        });


        Spinner yearSpinner = (Spinner) findViewById(R.id.year_input);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.years_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        Spinner quarterSpinner = (Spinner) findViewById(R.id.quarter_input);
        ArrayAdapter<CharSequence> quarterAdapter = ArrayAdapter.createFromResource(this,
                R.array.quarters_array, android.R.layout.simple_spinner_item);
        quarterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quarterSpinner.setAdapter(quarterAdapter);

    }

    public void onEnterClicked(View view) {
        int personId = 0;
        Spinner yearInput = findViewById(R.id.year_input);
        String courseYear = yearInput.getSelectedItem().toString();

        Spinner quarterInput = findViewById(R.id.quarter_input);
        String courseQuarter = quarterInput.getSelectedItem().toString();

        TextView subjectInput = findViewById(R.id.subject_input);
        String courseSubject = subjectInput.getText().toString();
        if(courseSubject.length() == 0){
            Utilities.showAlert(this, "Subject has not been entered!");
            return;
        }

        TextView nbrInput = findViewById(R.id.course_nbr_input);
        String courseNumber = nbrInput.getText().toString();
        if(courseNumber.length() == 0){
            Utilities.showAlert(this, "Course number has not been entered!");
            return;
        }

        this.future = backgroundThreadExecutor.submit(() -> {
            Course newCourse = new Course(personId, courseYear, courseQuarter, courseSubject, courseNumber);
            if(isDuplicate(newCourse, db.coursesDao().getForPerson(personId))){
                runOnUiThread(() -> {
                    Utilities.showAlert(this, "Duplicate Entry");
                });
                return null;
            }
            db.coursesDao().insert(newCourse);
            Log.i("Database", "Added course: " + newCourse.toString());
            counter++;

            return null;
        });

    }

    public boolean isDuplicate(Course newCourse, List<Course> courses){
        for(Course c: courses)
            if (c.year.equals(newCourse.year) && c.quarter.equals(newCourse.quarter) && c.subject.equals(newCourse.subject) && c.number.equals(newCourse.number)) {
                Log.i("database", "Duplicate course: " + c.toString());
                return true;
            }
        return false;
    }

    public void onDoneClicked(View view) {
        if (counter == 0) {
            Utilities.showAlert(this, "You must enter in at least one class!");
        } else {
            SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("Entered Classes", true);
            editor.apply();
            Intent intent = new Intent(this, ListingBOF.class);
            startActivity(intent);
        }
    }

}