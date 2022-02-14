package com.example.birdsoffeather;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

    public void addPersons() {
        testPersons = new ArrayList<>();
        Person user = new Person(0, "user", "");
        db.personsWithCoursesDao().insertPerson(user);
        ArrayList<Course> courses= new ArrayList<>();
        courses.add(new Course(0,"2021", "Fall", "CSE", "110"));
        courses.add(new Course(0,"2021", "Fall", "CSE", "10"));
        courses.add(new Course(0,"2021", "Fall", "CSE", "12"));

        db.coursesDao().insert(courses.get(0));
        db.coursesDao().insert(courses.get(1));
        db.coursesDao().insert(courses.get(2));

        testPersons.add(new PersonWithCourses(new Person(0,"person 1",""), courses));

        courses = new ArrayList<>();
        courses.add(new Course(0,"2021", "Fall", "ECE", "110"));
        courses.add(new Course(0,"2021", "Spring", "CSE", "10"));
        courses.add(new Course(0,"2021", "Fall", "CSE", "12"));

        testPersons.add(new PersonWithCourses(new Person(0,"person 2",""), courses));

        courses = new ArrayList<>();
        courses.add(new Course(0,"2019", "Fall", "CSE", "110"));
        courses.add(new Course(0,"2021", "Fall", "CSE", "10"));
        courses.add(new Course(0,"2021", "Fall", "CSE", "12"));

        testPersons.add(new PersonWithCourses(new Person(0,"person 3",""), courses));

        courses = new ArrayList<>();
        courses.add(new Course(0,"2019", "Spring", "MAE", "110"));
        courses.add(new Course(0,"2020", "Fall", "ECE", "10"));
        courses.add(new Course(0,"2015", "Spring", "MAE", "1"));

        testPersons.add(new PersonWithCourses(new Person(0,"person 4",""), courses));
    }

    @Test
    public void similarCourseTest() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addPersons();
                int similarClass = db.coursesDao().similarCourse("2021", "Fall", "CSE", "110");
                int notSimilarClass = db.coursesDao().similarCourse("2019", "Fall", "CSE", "110");

                assertNotEquals(0, similarClass); //There should be a match in the database
                assertEquals(0, notSimilarClass); //There should not be a match in the database

                db.clearAllTables();
                db.close();

            });
        });
    }


    @Test
    public void addOneBOFTest() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addPersons();

                //3 classes inputted by the user
                assertEquals(1, db.personsWithCoursesDao().count());
                assertEquals(3, db.coursesDao().count());


                Utilities.inputBOF(testPersons.get(0), db);

                assertEquals(2, db.personsWithCoursesDao()); //1 person added
                assertEquals(6, db.coursesDao().count()); // all 3 classes match, soo total of 6 classes


                db.clearAllTables();
                db.close();

                return null;
            });
        });
    }

    @Test
    public void addPersonWithoutCommonClassTest() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addPersons();

                //3 classes inputted by the user
                assertEquals(1, db.personsWithCoursesDao().count());
                assertEquals(3, db.coursesDao().count());


                Utilities.inputBOF(testPersons.get(0), db);

                assertEquals(2, db.personsWithCoursesDao()); //1 person added
                assertEquals(3, db.coursesDao().count()); //no classes added


                db.clearAllTables();
                db.close();

                return null;
            });
        });
    }



    @Test
    public void inputBOFTest() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addPersons();
                List<Course> before = db.coursesDao().getForPerson(1);
                assertEquals(0, before.size());
                for(int i = 0; i<testPersons.size(); i++) {
                    Utilities.inputBOF(testPersons.get(i), db);
                }

                List<Course> person1 = db.coursesDao().getForPerson(1);
                List<Course> person2 = db.coursesDao().getForPerson(2);
                List<Course> person3 = db.coursesDao().getForPerson(3);
                List<Course> person4 = db.coursesDao().getForPerson(4);

                //all classes match
                assertEquals(3, person1.size());

                //some classes match
                assertEquals(1, person2.size());
                assertEquals(2, person3.size());

                //no class matches
                assertEquals(0, person4.size());

                db.clearAllTables();
                db.close();

                return null;
            });

        });

    }

    @Test
    public  void getSimilarityOrderingTest(){
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addPersons();
                for (int i = 0; i < testPersons.size(); i++) {
                    Utilities.inputBOF(testPersons.get(i), db);
                }
                List<Integer> orderingByID = db.coursesDao().getSimilarityOrdering();
                assertEquals(1, (int) orderingByID.get(0));
                assertEquals(3, (int) orderingByID.get(1));
                assertEquals(2, (int) orderingByID.get(2));
            });
        });
    }

    @Test
    public void similarityOrderTest() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addPersons();
                for(int i = 0; i<testPersons.size(); i++) {
                    Utilities.inputBOF(testPersons.get(i), db);
                }

                List<PersonWithCourses> ordering = Utilities.generateSimilarityOrder(db);

                assertEquals(1, (int) ordering.get(0).person.personId);
                assertEquals(3, (int) ordering.get(1).person.personId);
                assertEquals(2, (int) ordering.get(2).person.personId);

                assertEquals(3, ordering.size()); //check that only 3 people had classes in course table

                db.clearAllTables();
                db.close();

                return null;
            });
        });
    }

    @Test
    public void similarityOrderNoInputTest() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addPersons();
                List<PersonWithCourses> ordering = Utilities.generateSimilarityOrder(db);

                assertEquals(0, ordering.size()); //check that no one is in the list
                db.clearAllTables();
                db.close();

                return null;
            });
        });
    }
    @Test
    public void removeBOFTest() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addPersons();
                int numUserClasses = db.coursesDao().getForPerson(0).size();

                //only the user's classes entered at this point
                assertEquals(numUserClasses, db.coursesDao().count());

                for(int i = 0; i<testPersons.size(); i++) {
                    Utilities.inputBOF(testPersons.get(i), db);
                }

                //courses of BOFs were added
                assertNotEquals(numUserClasses, db.coursesDao().count());

                //check that people were added correctly
                assertEquals(1 + testPersons.size(), db.personsWithCoursesDao().count());
                db.coursesDao().deleteBOFs();

                //Only the user's classes should remain at this point
                assertEquals(numUserClasses, db.coursesDao().count());

                //check that people were deleted properly
                assertEquals(1, db.personsWithCoursesDao().count());
                db.clearAllTables();
                db.close();
            });
        });
    }

    @Test
    public void removeBOFWithNoInputTest() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            backgroundThreadExecutor.submit(() -> {
                addPersons();
                int numUserClasses = db.coursesDao().getForPerson(0).size();

                //only the user's classes entered
                assertEquals(numUserClasses, db.coursesDao().count());

                db.coursesDao().deleteBOFs();

                //number of classes should not be affected
                assertEquals(numUserClasses, db.coursesDao().count());
                db.clearAllTables();
                db.close();
            });
        });
    }
}
