package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_classes);

        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());

            db.coursesDao().deleteAll();
            db.personsWithCoursesDao().deleteAll();

            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            String name = preferences.getString("name", "No Name");
            String url = preferences.getString("Photo URL", "No URL");

            Person user = new Person(0, name, url);
            db.personsWithCoursesDao().insertPerson(user);

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
        String courseSubject = subjectInput.getText().toString().trim().toLowerCase();
        if(courseSubject.length() == 0){
            Utilities.showAlert(this, "Subject has not been entered!");
            return;
        }

        TextView nbrInput = findViewById(R.id.course_nbr_input);
        String courseNumber = nbrInput.getText().toString().trim().toLowerCase();
        if(courseNumber.length() == 0){
            Utilities.showAlert(this, "Course number has not been entered!");
            return;
        }

        this.future = backgroundThreadExecutor.submit(() -> {
            int newCourseId = db.coursesDao().count() + 1;

            Course newCourse = new Course(newCourseId, personId, courseYear, courseQuarter, courseSubject, courseNumber);
            for(Course c: db.coursesDao().getForPerson(personId)){
                if(c.year.equals(courseYear) && c.quarter.equals(courseQuarter) && c.subject.equals(courseSubject) && c.number.equals(courseNumber)){
                    runOnUiThread(() -> {
                        Utilities.showAlert(this, "Duplicate Entry");
                    });
                    return null;
                }
            }
            db.coursesDao().insert(newCourse);

            return null;
        });

    }

    public void onDoneClicked(View view) {
        //Utilities.showAlert(this, db.personsWithCoursesDao().get(0).getName() + db.personsWithCoursesDao().get(0).getCourses());
        Utilities.showAlert(this, "done");
    }

}