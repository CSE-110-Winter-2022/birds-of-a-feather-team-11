package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.BluetoothMessageComposite;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NearbyMock extends AppCompatActivity {

    String userID;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_mock);

        db = AppDatabase.singleton(getApplicationContext());

        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        userID = preferences.getString("userID", null);

        TextView userIDText = findViewById(R.id.user_id);
        String displayString = "User ID: " + userID;
        userIDText.setText(displayString);
    }

    public void onAddButtonClicked(View view) {
        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        boolean isSessionRunning = preferences.getBoolean("isSessionRunning", false);
        if (!isSessionRunning) {
            Toast.makeText(this, "No session is actively running", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText personEntryEditText = findViewById(R.id.person_entry_edit_text);
        String entryData = personEntryEditText.getText().toString();
        BluetoothMessageComposite person = generatePerson(entryData);

        String sessionName = preferences.getString("currentSession", null);
        // If successful, clear text and add to database
        if (person != null) {
            Utilities.inputBOF(person.person, db, userID, sessionName);
            Utilities.updateWaves(db, userID, person.person.getId(), person.wavedToUUID);
            personEntryEditText.setText("");
            Toast.makeText(this, "Added person successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add - incorrect formatting", Toast.LENGTH_SHORT).show();
        }
        System.out.println(db.personsWithCoursesDao().getAll());

    }

    /*
    Helper method to create a PersonWithCourses object using a CSV-like input
    Returns null if
     */
    public static BluetoothMessageComposite generatePerson(String data) {
        String [] lines = data.split("\n");
        if (lines.length < 4) return null;
        String userID = lines[0].split(",")[0];
        String name = lines[1].split(",")[0];
        String uri = lines[2].split(",")[0];

        Person person = new Person(userID, name, uri, 0, 0, 0);
        List<Course> courseList = new ArrayList<>();

        int numCourses = 0;

        for (int i = 3; i < lines.length; i++) {
            if (lines[i].length() == 0) continue;
            String[] course = lines[i].split(",");
            if (course[1].equals("wave"))
                break;
            if (course.length != 5) {
                return null;
            }
            courseList.add(new Course(userID, course[0], course[1], course[2], course[3], course[4]));
            numCourses++;
        }

        if (numCourses == 0)
            return null;

        List<String> wavedToIDs = new ArrayList<>();

        for (int i = numCourses + 3; i < lines.length; i++) {
            if (lines[i].length() == 0) continue;
            String[] wavedPerson = lines[i].split(",");
            if (wavedPerson[1].equals("wave"))
                wavedToIDs.add(wavedPerson[0]);
        }
        PersonWithCourses personWithCourses = new PersonWithCourses();
        personWithCourses.person = person;
        personWithCourses.courses = courseList;
        System.out.println(courseList);

        return new BluetoothMessageComposite(personWithCourses, wavedToIDs);
    }
}