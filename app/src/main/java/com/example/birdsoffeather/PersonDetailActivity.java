package com.example.birdsoffeather;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import com.example.birdsoffeather.model.db.BluetoothMessageComposite;
import com.example.birdsoffeather.model.db.IPerson;
import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;

public class PersonDetailActivity extends AppCompatActivity {

    private AppDatabase db;
    private IPerson person;

    private RecyclerView coursesRecyclerView;
    private RecyclerView.LayoutManager coursesLayoutManager;
    private CourseViewAdapter coursesViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        Intent intent = getIntent();

        String personId = intent.getStringExtra("person_id");

        db = AppDatabase.singleton(this);
        person = db.personsWithCoursesDao().get(personId);
        List<Course> courses = person.getCourses();

        // set name
        TextView nameView = findViewById(R.id.personName);
        nameView.setText(person.getName());

        // set profile pic of person
        ImageView imageView = findViewById(R.id.pd_profilePic);
        LoadImage loadImage = new LoadImage(imageView);
        loadImage.execute(person.getUrl());

        // recycler view
        coursesRecyclerView = findViewById(R.id.course_view);
        coursesLayoutManager = new LinearLayoutManager(this);
        coursesRecyclerView.setLayoutManager(coursesLayoutManager);

        coursesViewAdapter = new CourseViewAdapter(courses);
        coursesRecyclerView.setAdapter(coursesViewAdapter);

        if (person.sentWaveTo()) {
            updateWaveButton();
        }

    }

    public void onGoBackClicked(View view) {
        finish();
    }

    public void onWaved(View view) {

        // Update Database
        String personID = person.getId();
        db.personsWithCoursesDao().updateSentWaveTo(personID);

        // Update Bluetooth Message
        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        String selfID = preferences.getString("userID", null);

        PersonWithCourses selfPerson = db.personsWithCoursesDao().get(selfID);
        List<String> sentWaveTo = db.personsWithCoursesDao().getSentWaveTo();

        updateBluetoothMessage(selfPerson, sentWaveTo);

        // Update UI
        updateWaveButton();

        // Send Toast
        Toast.makeText(this, "Wave sent!", Toast.LENGTH_SHORT).show();

    }

    private void updateWaveButton() {
        Button waveButton = findViewById(R.id.wave_button);
        waveButton.setText("WAVED");
        waveButton.setEnabled(false);
    }

    private void updateBluetoothMessage(PersonWithCourses selfPerson, List<String> sentWaveTo) {
        BluetoothModule bluetoothModule = BluetoothModule.getBluetoothSingleton();
        bluetoothModule.unpublish();

        byte [] message = new byte[0];
        try {
            message = Utilities.serializeMessage(selfPerson, sentWaveTo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothModule.setMessage(new Message(message));
        bluetoothModule.publish();
    }
}