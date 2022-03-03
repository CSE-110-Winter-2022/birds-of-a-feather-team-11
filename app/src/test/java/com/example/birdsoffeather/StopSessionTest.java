package com.example.birdsoffeather;
import static android.content.Context.MODE_PRIVATE;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;

import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.Session;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class StopSessionTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    @Rule
    public ActivityScenarioRule<StopSave> scenarioRule = new ActivityScenarioRule<>(StopSave.class);
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

    public void createSession(String sessionName) {
        db.sessionsDao().insert(new Session(sessionName, userID));
    }

    @Test
    public void getCurrCoursesTest() {
        ActivityScenario<StopSave> scenario1 = scenarioRule.getScenario();
        scenario1.moveToState(Lifecycle.State.CREATED);

        scenario1.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addUser();
                addUserClasses();
                List<Course> courses = new ArrayList<>();
                courses.add(new Course(userID,"2022", "Winter", "CSE", "110", "Tiny (<40)"));
                courses.add(new Course(userID,"2022", "Winter", "CSE", "10", "Tiny (<40)"));
                List<Course> currCourses = activity.getCurrCourses(2022, 0, userID);
                activity.runOnUiThread(() -> {
                    assertEquals(courses.toString(), currCourses.toString());
                });
            });
        });
    }

    @Test
    public void getAvailableSessionTest() {

        ActivityScenario<StopSave> scenario1 = scenarioRule.getScenario();
        scenario1.moveToState(Lifecycle.State.CREATED);

        scenario1.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addUser();
                addUserClasses();

                SharedPreferences preferences = activity.getSharedPreferences("BoF", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userID", userID);
                editor.apply();

                List<String> courses = new ArrayList<>();
                courses.add("CSE 110");
                courses.add("CSE 10");
                courses.add("Other Name");
                List<String> availableCourses = activity.getAvailableCourses();
                activity.runOnUiThread(() -> {
                    assertEquals(courses.toString(), availableCourses.toString());
                });

                createSession("CSE 110");
                courses.remove("CSE 110");
                List<String> availableCourses2 = activity.getAvailableCourses();
                activity.runOnUiThread(() -> {
                    assertEquals(courses.toString(), availableCourses2.toString());
                });


                db.clearAllTables();
                db.close();

            });
        });

    }

    /*
    @Test
    public void saveSessionTest() {
        ActivityScenario<ListingBOF> scenario2 = scenarioRule2.getScenario();

        scenario2.moveToState(Lifecycle.State.CREATED);

        scenario2.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addUser();
                addUserClasses();
            });
        });


        ActivityScenario<StopSave> scenario1 = scenarioRule.getScenario();
        scenario1.moveToState(Lifecycle.State.CREATED);

        scenario1.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {

            });
        });
    }
     */
}
