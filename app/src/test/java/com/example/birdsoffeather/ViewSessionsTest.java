package com.example.birdsoffeather;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;

import org.junit.Rule;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewSessionsTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    @Rule
    public ActivityScenarioRule<ViewSessions> scenarioRule = new ActivityScenarioRule<>(ViewSessions.class);

    @Rule
    public ActivityScenarioRule<ListingBOF> scenarioRule2 = new ActivityScenarioRule<>(ListingBOF.class);

    String userID = UUID.randomUUID().toString();
    AppDatabase db;

    public void addUser() {
        Person user = new Person(userID, "user", "", 0, 0);
        db.personsWithCoursesDao().insertPerson(user);
    }
    public void addUserClasses() {
        db.coursesDao().insert(new Course(userID,"2022", "Winter", "CSE", "110", "Tiny (<40)"));
        db.coursesDao().insert(new Course(userID,"2022", "Winter", "CSE", "10", "Tiny (<40)"));
        db.coursesDao().insert(new Course(userID,"2021", "Winter", "CSE", "20", "Tiny (<40)"));
        db.coursesDao().insert(new Course(userID,"2022", "Fall", "CSE", "21", "Tiny (<40)"));
        db.coursesDao().insert(new Course(userID,"2022", "Fall", "CSE", "110", "Tiny (<40)"));
    }


}
