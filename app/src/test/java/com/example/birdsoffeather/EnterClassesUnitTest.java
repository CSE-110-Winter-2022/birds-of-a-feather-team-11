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
import org.robolectric.util.Util;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(AndroidJUnit4.class)
public class EnterClassesUnitTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    @Rule
    public ActivityScenarioRule<EnterClasses> scenarioRule = new ActivityScenarioRule<>(EnterClasses.class);

    String userID = UUID.randomUUID().toString();


    @Test
    public void testEnterWithNoInput() {

        ActivityScenario<EnterClasses> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                List<Course> before = db.coursesDao().getForPerson(userID);

                assertEquals(0, before.size());

                Button enterButton = activity.findViewById(R.id.enter_btn);
                enterButton.performClick();
                List<Course> after = db.coursesDao().getForPerson(userID);

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
                List<Course> before = db.coursesDao().getForPerson(userID);

                assertEquals(1, before.size());//1 class should be added

                enterButton.performClick();
                List<Course> after = db.coursesDao().getForPerson(userID);

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
                Spinner classSize_spinner = activity.findViewById(R.id.class_size_input);

                subjectView.setText("CSE");
                numView.setText("110");
                Button enterButton = activity.findViewById(R.id.enter_btn);
                enterButton.performClick();

                yearSpinner.setSelection(1);
                enterButton.performClick();

                quarterSpinner.setSelection(2);
                enterButton.performClick();

                subjectView.setText("ECE");
                classSize_spinner.setSelection(1);
                enterButton.performClick();

                numView.setText("101A");
                classSize_spinner.setSelection(4);
                enterButton.performClick();

                List<Course> addedCourses = db.coursesDao().getForPerson(userID);
                assertEquals(5, addedCourses.size());

                List<Course> expectedCourses = new ArrayList<>();
                expectedCourses.add(new Course(userID, "2022", "Fall", "CSE", "110", "Tiny (20)"));
                expectedCourses.add(new Course(userID, "2021", "Fall", "CSE", "110", "Tiny (20)"));
                expectedCourses.add(new Course(userID, "2021", "Spring", "CSE", "110", "Tiny (20)"));
                expectedCourses.add(new Course(userID, "2021", "Spring", "ECE", "110", "Small (60)"));
                expectedCourses.add(new Course(userID, "2021", "Spring", "ECE", "101A", "Huge (325)"));

                assertEquals(expectedCourses.toString(), addedCourses.toString());
                return null;
            });
        });

    }

    @Test
    public void testIsDuplicateTrue() {
        List<Course> courses = Arrays.asList(
                new Course(userID, "2019", "Winter", "CSE", "1", "Tiny (20)"),
                new Course(userID, "2020", "Fall", "CSE", "2", "Tiny (20)"),
                new Course(userID, "2022", "Winter", "CSE", "110", "Large (200)"));

        Course cse110Duplicate = new Course(userID, "2022", "Winter", "CSE", "110", "Large (200)");

        assertTrue(Utilities.isDuplicate(cse110Duplicate, courses));
    }

    @Test
    public void testIsDuplicateFalse() {
        List<Course> courses = Arrays.asList(
                new Course(userID, "2019", "Winter", "CSE", "1", "Tiny (20)"),
                new Course(userID, "2020", "Fall", "CSE", "2", "Tiny (20)"),
                new Course(userID, "2022", "Winter", "CSE", "110", "Large (200)"));

        Course cse110DiffYear = new Course(userID, "2021", "Winter", "CSE", "110", "Large (200)");
        Course cse110DiffQuarter = new Course(userID, "2022", "Fall", "CSE", "110", "Large (200)");
        Course cse110DiffSubject = new Course(userID, "2022", "Winter", "ECE", "110", "Large (200)");
        Course cse110DiffNumber = new Course(userID, "2022", "Winter", "CSE", "100", "Large (200)");
        Course cse110DiffClassSize = new Course(userID, "2022", "Winter", "CSE", "110", "Huge (325)");

        assertFalse(Utilities.isDuplicate(cse110DiffYear, courses));
        assertFalse(Utilities.isDuplicate(cse110DiffQuarter, courses));
        assertFalse(Utilities.isDuplicate(cse110DiffSubject, courses));
        assertFalse(Utilities.isDuplicate(cse110DiffNumber, courses));
        assertFalse(Utilities.isDuplicate(cse110DiffClassSize, courses));

    }

    @Test
    public void testDoneWithNoClasses() {
        ActivityScenario<EnterClasses> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                List<Course> before = db.coursesDao().getForPerson(userID);

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