package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;

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

        if (person != null) {
            //Utilities.inputBOF(person, AppDatabase.singleton(getApplicationContext()));
            personEntryEditText.setText("");
            Toast.makeText(this, "Added person successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add - incorrect formatting", Toast.LENGTH_SHORT).show();
        }

    }

    public PersonWithCourses generatePerson(String data) {
        String [] lines = data.split("\n");
        if (lines.length < 3) return null;
        String name = lines[0].split(",")[0];
        String uri = lines[1].split(",")[0];
        Person person = new Person(0, name, uri);
        List<Course> courseList = new ArrayList<>();

        for (int i = 2; i < lines.length; i++) {
            if (lines[i].length() == 0) continue;
            String [] course = lines[i].split(",");
            if (course.length != 4) {
                return null;
            }
            courseList.add(new Course(0, 0, course[0], course[1], course[2], course[3]));

        }

        PersonWithCourses personWithCourses = new PersonWithCourses();
        personWithCourses.person = person;
        personWithCourses.courses = courseList;

        return personWithCourses;
    }
}