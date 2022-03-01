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
import java.util.UUID;
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
            String name = preferences.getString("Name", "No Name");
            String url = preferences.getString("Photo URL", "No URL");
            String userID = UUID.randomUUID().toString();
            Person user = new Person(userID, name, url);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userID", userID);
            editor.apply();
            db.personsWithCoursesDao().insertPerson(user);
            Log.i("Database", "Added person: " + user.toString());

            return null;
        });

        // year spinner
        Spinner yearSpinner = (Spinner) findViewById(R.id.year_input);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.years_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // quarter spinner
        Spinner quarterSpinner = (Spinner) findViewById(R.id.quarter_input);
        ArrayAdapter<CharSequence> quarterAdapter = ArrayAdapter.createFromResource(this,
                R.array.quarters_array, android.R.layout.simple_spinner_item);
        quarterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quarterSpinner.setAdapter(quarterAdapter);

        // class size spinner
        Spinner classSize_spinner = (Spinner) findViewById(R.id.class_size_input);
        ArrayAdapter<CharSequence> classSize_adapter = ArrayAdapter.createFromResource(this,
                R.array.class_sizes, android.R.layout.simple_spinner_item);
        classSize_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSize_spinner.setAdapter(classSize_adapter);
    }

    /**
     * Inputs the filled out class into the database when enter is clicked
     *
     * @param view
     */
    public void onEnterClicked(View view) {
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

        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        String userID = preferences.getString("userID", null);

        this.future = backgroundThreadExecutor.submit(() -> {
            Course newCourse = new Course(userID, courseYear, courseQuarter, courseSubject, courseNumber);
            if(Utilities.isDuplicate(newCourse, db.coursesDao().getForPerson(userID))){
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

    /**
     * Indicates that the user's classes have been entered when the done button is clicked.
     *
     * @param view
     */
    public void onDoneClicked(View view) {
        if (counter == 0) {
            Utilities.showAlert(this, "You must enter in at least one class!");
        } else {
            SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("Entered Classes", true);
            editor.apply();
            Log.i("Shared Preferences", "Done adding classes");
            Intent intent = new Intent(this, ListingBOF.class);
            startActivity(intent);
        }
    }

}