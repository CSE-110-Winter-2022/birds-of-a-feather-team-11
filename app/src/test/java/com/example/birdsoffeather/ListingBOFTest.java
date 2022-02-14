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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class ListingBOFTest {

    @Rule
    public ActivityScenarioRule<ListingBOF> scenarioRule = new ActivityScenarioRule<>(ListingBOF.class);

    @Test
    public void testUIRefresh() {
        Person person1 = new Person(0, "John", "url");
        ArrayList<Course> courses= new ArrayList<>();
        courses.add(new Course(0,"2021", "Spring", "CSE", "110"));
        courses.add(new Course(1,"2021", "Winter", "CSE", "100"));
        courses.add(new Course(2,"2020", "Spring", "CSE", "12"));
        PersonWithCourses personWithCourses1 = new PersonWithCourses();
        personWithCourses1.courses = courses;
        personWithCourses1.person = person1;

        Person person2 = new Person(0, "John", "url");
        PersonWithCourses personWithCourses2 = new PersonWithCourses();
        personWithCourses2.courses = courses;
        personWithCourses2.person = person2;

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            activity.updateUI(Arrays.asList(
                    personWithCourses1,
                    personWithCourses2
            ));

            assertEquals(2, activity.getPersonsViewAdapter().getItemCount());
        });
    }

    @Test
    public void testUIRefreshEmptyList() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            activity.updateUI(new ArrayList<>());

            assertEquals(0, activity.getPersonsViewAdapter().getItemCount());
        });
    }


}
