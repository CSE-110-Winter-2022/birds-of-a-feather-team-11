package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_bof);
    }


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
            List<PersonWithCourses> orderedBOFs = orderedIds.stream().map((id) -> db.personsWithCoursesDao().get(id)).collect(Collectors.toList());
            //updateUI(orderedBOFs);
            return null;
        });
    }
}