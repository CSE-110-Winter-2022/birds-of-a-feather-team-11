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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class DBQueryingUnitTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    List<PersonWithCourses> testPersons;
    AppDatabase db;
    @Rule
    public ActivityScenarioRule<ListingBOF> scenarioRule = new ActivityScenarioRule<>(ListingBOF.class);

    @Before
    public void addPersons() {
        testPersons = new ArrayList<>();
        db = AppDatabase.singleton(getApplicationContext());
        Person user = new Person(0, "user", "");
        db.personsWithCoursesDao().insertPerson(user);
        ArrayList<Course> courses= new ArrayList<>();
        courses.add(new Course(0,0,"2021", "Fall", "CSE", "110"));
        courses.add(new Course(1,0,"2021", "Fall", "CSE", "10"));
        courses.add(new Course(2,0,"2021", "Fall", "CSE", "12"));

        db.coursesDao().insert(courses.get(0));
        db.coursesDao().insert(courses.get(1));
        db.coursesDao().insert(courses.get(2));


        testPersons.add(new PersonWithCourses(new Person(0,"person1",""), courses));

        courses = new ArrayList<>();
        courses.add(new Course(0,0,"2021", "Fall", "ECE", "110"));
        courses.add(new Course(1,0,"2021", "Spring", "CSE", "10"));
        courses.add(new Course(2,0,"2021", "Fall", "CSE", "12"));

        testPersons.add(new PersonWithCourses(new Person(0,"person2",""), courses));

        courses = new ArrayList<>();
        courses.add(new Course(0,0,"2019", "Fall", "CSE", "110"));
        courses.add(new Course(1,0,"2021", "Fall", "CSE", "10"));
        courses.add(new Course(2,0,"2021", "Fall", "CSE", "12"));

        testPersons.add(new PersonWithCourses(new Person(0,"person3",""), courses));

        courses = new ArrayList<>();
        courses.add(new Course(0,0,"2019", "Spring", "MAE", "110"));
        courses.add(new Course(1,0,"2020", "Fall", "ECE", "10"));
        courses.add(new Course(2,0,"2015", "Spring", "MAE", "1"));

        testPersons.add(new PersonWithCourses(new Person(0,"person 4",""), courses));
    }


    @Test
    public void bofTest() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());
            List<Course> before = db.coursesDao().getForPerson(1);
            assertEquals(0, before.size());
            for(int i = 0; i<testPersons.size(); i++){
                activity.inputBOF(testPersons.get(i));
            }

            List<Course> person1 = db.coursesDao().getForPerson(1);
            List<Course> person2 = db.coursesDao().getForPerson(2);
            List<Course> person3 = db.coursesDao().getForPerson(3);
            List<Course> person4 = db.coursesDao().getForPerson(4);

            assertEquals(3, person1.size());
            assertEquals(1, person2.size());
            assertEquals(2, person3.size());
            assertEquals(0, person4.size());

            List<Integer> ordering = db.coursesDao().getSimilarityOrdering();

            assertEquals(1, (int) ordering.get(0));
            assertEquals(3, (int) ordering.get(1));
            assertEquals(2, (int) ordering.get(2));

            assertEquals(3, ordering.size()); //check that only 3 people had classes in course table

        });
    }
}
