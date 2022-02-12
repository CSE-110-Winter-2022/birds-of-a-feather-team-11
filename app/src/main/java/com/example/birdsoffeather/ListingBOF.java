package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.IPerson;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ListingBOF extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    private List<PersonWithCourses> orderedBOFs = new ArrayList<>();

    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_bof);


        Button start_stop_btn = (Button) findViewById(R.id.start_stop_btn);
        start_stop_btn.setText("START"); // default button text
        start_stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // changes button color
                start_stop_btn.setSelected(!start_stop_btn.isSelected());

                // START button clicked
                if(start_stop_btn.getText().toString() == "START") {
                    // button text changes to STOP once START is clicked
                    start_stop_btn.setText("STOP");

                    // show list of students
                    updateUI(orderedBOFs);
                }
                // STOP button clicked
                else {
                    // button text changes to START once STOP is clicked
                    start_stop_btn.setText("START");
                }
            }
        });


        }

    public void updateUI(List<? extends IPerson> persons) {
        personsRecyclerView.findViewById(R.id.persons_view);

        // set layout manager
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        // set adapter
        personsViewAdapter = new PersonsViewAdapter(persons);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }



    /*
    public void inputBOF(PersonWithCourses potentialBOF) {
        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());
            Person userInfo = potentialBOF.person;
            int personId = db.personsWithCoursesDao().count();
            Person user = new Person(personId, userInfo.name, userInfo.profile_url);
            db.personsWithCoursesDao().insertPerson(user);
            List<Course> courses = potentialBOF.getCourses();
            for (Course course : courses) {
                if (db.coursesDao().similarCourse(course.year, course.quarter, course.subject, course.number) != 0)
                    db.coursesDao().insert(new Course(db.coursesDao().count(), personId, course.year, course.quarter, course.subject, course.number));
            }
            return null;
        });

        // call function to re-render UI
    }


    public void similarityOrder() {
        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());
            List<Integer> orderedIds = db.coursesDao().getSimilarityOrdering();
            orderedBOFs = orderedIds.stream().map((id) -> db.personsWithCoursesDao().get(id)).collect(Collectors.toList());
            return null;
        });
    }*/
}