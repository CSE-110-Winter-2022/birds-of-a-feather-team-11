package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NearbyMock extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_mock);
    }


    public void onAddButtonClicked(View view) {
        EditText personEntryEditText = findViewById(R.id.person_entry_edit_text);
        String entryData = personEntryEditText.getText().toString();
        PersonWithCourses person = generatePerson(entryData);

        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        String userID = preferences.getString("userID", null);
        String sessionName = preferences.getString("currentSession", null);
        // If successful, clear text and add to database
        if (person != null) {
            Utilities.inputBOF(person, AppDatabase.singleton(getApplicationContext()), userID, sessionName);
            personEntryEditText.setText("");
            Toast.makeText(this, "Added person successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add - incorrect formatting", Toast.LENGTH_SHORT).show();
        }
        AppDatabase db = AppDatabase.singleton(this);
        System.out.println(db.personsWithCoursesDao().getAll());

    }

    /*
    Helper method to create a PersonWithCourses object using a CSV-like input
    Returns null if
    TODO: account for UUIDS and class sizes
     */
    public static PersonWithCourses generatePerson(String data) {
        String [] lines = data.split("\n");
        if (lines.length < 3) return null;
        String name = lines[0].split(",")[0];
        String uri = lines[1].split(",")[0];

        // replace with UUID
        Person person = new Person("replace with UUID", name, uri, 0, 0);
        List<Course> courseList = new ArrayList<>();

        for (int i = 2; i < lines.length; i++) {
            if (lines[i].length() == 0) continue;
            String [] course = lines[i].split(",");
            if (course.length != 5) {
                return null;
            }
            courseList.add(new Course("replace with UUID", course[0], course[1], course[2], course[3], course[4]));

        }
        PersonWithCourses personWithCourses = new PersonWithCourses();
        personWithCourses.person = person;
        personWithCourses.courses = courseList;
        System.out.println(courseList);

        return personWithCourses;
    }
}