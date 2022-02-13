package com.example.birdsoffeather;


import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(AndroidJUnit4.class)
public class EnterClassesUnitTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    @Rule
    public ActivityScenarioRule<EnterClasses> scenarioRule = new ActivityScenarioRule<>(EnterClasses.class);


    @Test
    public void testEnterWithNoInput() {

        ActivityScenario<EnterClasses> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                List<Course> before = db.coursesDao().getForPerson(0);

                assertEquals(0, before.size());

                Button enterButton = activity.findViewById(R.id.enter_btn);
                enterButton.performClick();
                List<Course> after = db.coursesDao().getForPerson(0);

                assertEquals(0, after.size());
                return null;
            });
        });
    }

    //Repeat Class Input
    @Test
    public void testDuplicate() {
        ActivityScenario<EnterClasses> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                EditText subjectView = activity.findViewById(R.id.subject_input);
                EditText numView = activity.findViewById(R.id.course_nbr_input);
                subjectView.setText("CSE");
                numView.setText("110");
                Button enterButton = activity.findViewById(R.id.enter_btn);
                enterButton.performClick();
                List<Course> before = db.coursesDao().getForPerson(0);

                assertEquals(1, before.size());//1 class should be added

                enterButton.performClick();
                List<Course> after = db.coursesDao().getForPerson(0);

                assertEquals(before, after);
                return null;
            });
        });
    }

    //Valid Class Enter
    @Test
    public void testNormalAdding() {
        ActivityScenario<EnterClasses> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                EditText subjectView = activity.findViewById(R.id.subject_input);
                EditText numView = activity.findViewById(R.id.course_nbr_input);
                Spinner yearSpinner = activity.findViewById(R.id.year_input);
                Spinner quarterSpinner = activity.findViewById(R.id.quarter_input);

                subjectView.setText("CSE");
                numView.setText("110");
                Button enterButton = activity.findViewById(R.id.enter_btn);
                enterButton.performClick();

                yearSpinner.setSelection(1);
                enterButton.performClick();

                quarterSpinner.setSelection(2);
                enterButton.performClick();

                subjectView.setText("ECE");
                enterButton.performClick();

                numView.setText("101A");
                enterButton.performClick();

                List<Course> addedCourses = db.coursesDao().getForPerson(0);
                assertEquals(5, addedCourses.size());

                List<Course> expectedCourses = new ArrayList<>();
                expectedCourses.add(new Course(0, 0, "2022", "Fall", "CSE", "110"));
                expectedCourses.add(new Course(1, 0, "2021", "Fall", "CSE", "110"));
                expectedCourses.add(new Course(2, 0, "2021", "Spring", "CSE", "110"));
                expectedCourses.add(new Course(3, 0, "2021", "Spring", "ECE", "110"));
                expectedCourses.add(new Course(4, 0, "2021", "Spring", "ECE", "101A"));

                assertEquals(expectedCourses.toString(), addedCourses.toString());
                return null;
            });
        });

    }

    @Test
    public void testIsDuplicateTrue() {
        List<Course> courses = Arrays.asList(
                new Course(1, 0, "2019", "Winter", "CSE", "1"),
                new Course(1, 0, "2020", "Fall", "CSE", "2"),
                new Course(0, 0, "2022", "Winter", "CSE", "110"));

        Course cse110Duplicate = new Course(0, 1, "2022", "Winter", "CSE", "110");

        assertTrue(EnterClasses.isDuplicate(cse110Duplicate, courses));
    }

    @Test
    public void testIsDuplicateFalse() {
        List<Course> courses = Arrays.asList(
                new Course(1, 0, "2019", "Winter", "CSE", "1"),
                new Course(1, 0, "2020", "Fall", "CSE", "2"),
                new Course(0, 0, "2022", "Winter", "CSE", "110"));

        Course cse110DiffYear = new Course(0, 1, "2021", "Winter", "CSE", "110");
        Course cse110DiffQuarter = new Course(0, 1, "2022", "Fall", "CSE", "110");
        Course cse110DiffSubject = new Course(0, 1, "2022", "Winter", "ECE", "110");
        Course cse110DiffNumber = new Course(0, 1, "2022", "Winter", "CSE", "100");

        assertFalse(EnterClasses.isDuplicate(cse110DiffYear, courses));
        assertFalse(EnterClasses.isDuplicate(cse110DiffQuarter, courses));
        assertFalse(EnterClasses.isDuplicate(cse110DiffSubject, courses));
        assertFalse(EnterClasses.isDuplicate(cse110DiffNumber, courses));

    }

    @Test
    public void testDoneWithNoClasses() {
        ActivityScenario<EnterClasses> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                List<Course> before = db.coursesDao().getForPerson(0);

                assertEquals(0, before.size());

                Button doneButton = activity.findViewById(R.id.done_btn);
                doneButton.performClick();

                SharedPreferences preferences = activity.getSharedPreferences("BoF", Context.MODE_PRIVATE);
                assertFalse(preferences.getBoolean("Entered Classes", false));
                return null;
            });
        });
    }

    @Test
    public void testDoneWithClassesEntered() {
        ActivityScenario<EnterClasses> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            backgroundThreadExecutor.submit(() -> {
                EditText subjectView = activity.findViewById(R.id.subject_input);
                EditText numView = activity.findViewById(R.id.course_nbr_input);

                subjectView.setText("CSE");
                numView.setText("110");
                Button enterButton = activity.findViewById(R.id.enter_btn);
                enterButton.performClick();

                Button doneButton = activity.findViewById(R.id.done_btn);
                doneButton.performClick();

                SharedPreferences preferences = activity.getSharedPreferences("BoF", Context.MODE_PRIVATE);
                assertTrue(preferences.getBoolean("Entered Classes", false));
                return null;
            });
        });
    }

    @Test
    public void testPersonAddedOnCreate() {
        ActivityScenario<EnterClasses> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            backgroundThreadExecutor.submit(() -> {
                EditText subjectView = activity.findViewById(R.id.subject_input);
                EditText numView = activity.findViewById(R.id.course_nbr_input);

                subjectView.setText("CSE");
                numView.setText("110");
                Button enterButton = activity.findViewById(R.id.enter_btn);
                enterButton.performClick();

                Button doneButton = activity.findViewById(R.id.done_btn);
                doneButton.performClick();

                SharedPreferences preferences = activity.getSharedPreferences("BoF", Context.MODE_PRIVATE);
                assertTrue(preferences.getBoolean("Entered Classes", false));
                return null;
            });
        });
    }
}