package com.example.birdsoffeather;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;

import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.MessageListener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class BluetoothTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    @Rule
    public ActivityScenarioRule<ListingBOF> scenarioRule = new ActivityScenarioRule<>(ListingBOF.class);

    @Test
    public void testFakePersonSend() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            PersonWithCourses fakePerson = new PersonWithCourses();
            fakePerson.person = new Person(0, "John", "www.google.com");
            List<Course> courses = new ArrayList<Course>();
            courses.add(new Course(1, 0, "2022", "Winter", "CSE", "110"));
            fakePerson.courses = courses;
            MessageListener fake = new FakeMessageListener(activity.getMessageListener(), fakePerson);
            activity.setMessageListener(fake);

            backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.useTestSingleton(getApplicationContext());

                int numPeople = db.personsWithCoursesDao().count();
                int numCourses = db.coursesDao().count();

                assertEquals(1, numPeople);
                assertEquals(1, numCourses);

                String personName = db.personsWithCoursesDao().get(0).person.name;

                assertEquals("John", personName);

                return null;
            });
        });
    }
}
