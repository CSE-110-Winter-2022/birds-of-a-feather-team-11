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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RunWith(AndroidJUnit4.class)
public class DBQueryingUnitTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    List<PersonWithCourses> testPersons;
    List<String> testIds;
    AppDatabase db;

    String userID = UUID.randomUUID().toString();

    @Rule
    public ActivityScenarioRule<ListingBOF> scenarioRule = new ActivityScenarioRule<>(ListingBOF.class);

    @Before
    public void addPersons() {
        testPersons = new ArrayList<>();
        testIds = new ArrayList<>();

        for (int i = 0; i < 4; i++)
            testIds.add(UUID.randomUUID().toString());

        ArrayList<Course> courses = new ArrayList<>();
        courses.add(new Course(testIds.get(0),"2021", "Fall", "CSE", "110", "Large (150-250)"));
        courses.add(new Course(testIds.get(0),"2021", "Fall", "CSE", "10", "Tiny (<40)"));
        courses.add(new Course(testIds.get(0),"2021", "Fall", "CSE", "12", "Medium (75-150)"));

        testPersons.add(new PersonWithCourses(new Person(testIds.get(0),"person 1","", 0, 0, 0), courses));

        courses = new ArrayList<>();
        courses.add(new Course(testIds.get(1),"2021", "Fall", "ECE", "110", "Tiny (<40)"));
        courses.add(new Course(testIds.get(1),"2021", "Spring", "CSE", "10", "Tiny (<40)"));
        courses.add(new Course(testIds.get(1),"2021", "Fall", "CSE", "12", "Medium (75-150)"));

        testPersons.add(new PersonWithCourses(new Person(testIds.get(1),"person 2","", 0, 0, 0), courses));

        courses = new ArrayList<>();
        courses.add(new Course(testIds.get(2),"2019", "Fall", "CSE", "110", "Huge (250-400)"));
        courses.add(new Course(testIds.get(2),"2021", "Fall", "CSE", "10", "Tiny (<40)"));
        courses.add(new Course(testIds.get(2),"2021", "Fall", "CSE", "12", "Medium (75-150)"));

        testPersons.add(new PersonWithCourses(new Person(testIds.get(2),"person 3","", 0, 0, 0), courses));

        courses = new ArrayList<>();
        courses.add(new Course(testIds.get(3),"2019", "Spring", "MAE", "110", "Large (150-250)"));
        courses.add(new Course(testIds.get(3),"2020", "Fall", "ECE", "10", "Small (40-75)"));
        courses.add(new Course(testIds.get(3),"2015", "Spring", "MAE", "1", "Gigantic (400+)"));

        testPersons.add(new PersonWithCourses(new Person(testIds.get(3),"person 4","", 0, 0, 0), courses));
    }

    public void addUser() {
        Person user = new Person(userID, "user", "", 0, 0, 0);
        db.personsWithCoursesDao().insertPerson(user);
        ArrayList<Course> courses= new ArrayList<>();
        courses.add(new Course(userID,"2021", "Fall", "CSE", "110", "Large (150-250)"));
        courses.add(new Course(userID,"2021", "Fall", "CSE", "10", "Tiny (<40)"));
        courses.add(new Course(userID,"2021", "Fall", "CSE", "12", "Medium (75-150)"));

        db.coursesDao().insert(courses.get(0));
        db.coursesDao().insert(courses.get(1));
        db.coursesDao().insert(courses.get(2));
    }

    public List<List<Course>> generateSimilarCourses() {
        return testPersons.stream().map(person -> Utilities.generateSimilarCourses(person, db, userID)).collect(Collectors.toList());
    }

    @Test
    public void similarCourseTest() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();
                int similarClass = db.coursesDao().similarCourseNum(userID, "2021", "Fall", "CSE", "110");
                int notSimilarClass = db.coursesDao().similarCourseNum(userID, "2019", "Fall", "CSE", "110");

                assertNotEquals(0, similarClass); //There should be a match in the database
                assertEquals(0, notSimilarClass); //There should not be a match in the database

                db.clearAllTables();
                db.close();

            });
            Utilities.waitForThread(future);
        });
    }


    @Test
    public void addOneBOFTest() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();

                final int personCount = db.personsWithCoursesDao().count();
                final int courseCount = db.coursesDao().count();

                activity.runOnUiThread(() -> {
                    //3 classes inputted by the user
                    assertEquals(1, personCount);
                    assertEquals(3, courseCount);
                });

                Utilities.inputBOF(testPersons.get(0), db, userID, "test");

                final int personCount2 = db.personsWithCoursesDao().count();
                final int courseCount2 = db.coursesDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(2, personCount2); //1 person added
                    assertEquals(6, courseCount2); // all 3 classes match, soo total of 6 classes
                });

                db.clearAllTables();
                db.close();

                return null;
            });
            Utilities.waitForThread(future);
        });
    }

    @Test
    public void addPersonWithoutCommonClassTest() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();

                final int personCount = db.personsWithCoursesDao().count();
                final int courseCount = db.coursesDao().count();

                activity.runOnUiThread(() -> {
                    //3 classes inputted by the user
                    assertEquals(1, personCount);
                    assertEquals(3, courseCount);
                });

                Utilities.inputBOF(testPersons.get(3), db, userID, "test");

                final int personCount2 = db.personsWithCoursesDao().count();
                final int courseCount2 = db.coursesDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(2, personCount2); //1 person added
                    assertEquals(3, courseCount2); //no classes added
                });

                db.clearAllTables();
                db.close();

                return null;
            });
            Utilities.waitForThread(future);
        });
    }



    @Test
    public void inputBOFTest() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();
                List<Course> before = db.coursesDao().getForPerson(testIds.get(0));
                activity.runOnUiThread(() -> {
                    assertEquals(0, before.size());
                });
                for(int i = 0; i<testPersons.size(); i++) {
                    Utilities.inputBOF(testPersons.get(i), db, userID, "test");
                }

                List<Course> person1 = db.coursesDao().getForPerson(testIds.get(0));
                List<Course> person2 = db.coursesDao().getForPerson(testIds.get(1));
                List<Course> person3 = db.coursesDao().getForPerson(testIds.get(2));
                List<Course> person4 = db.coursesDao().getForPerson(testIds.get(3));

                activity.runOnUiThread(() -> {
                    //all classes match
                    assertEquals(3, person1.size());

                    //some classes match
                    assertEquals(1, person2.size());
                    assertEquals(2, person3.size());

                    //no class matches
                    assertEquals(0, person4.size());
                });

                db.clearAllTables();
                db.close();

                return null;
            });
            Utilities.waitForThread(future);
        });

    }

    @Test
    public void getSimilarityOrderingTest(){
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();
                for (int i = 0; i < testPersons.size(); i++) {
                    Utilities.inputBOF(testPersons.get(i), db, userID, "test");
                }
                List<String> orderingByID = db.coursesDao().getSimilarityOrdering(userID);

                activity.runOnUiThread(() -> {
                    assertEquals(testIds.get(0), orderingByID.get(0));
                    assertEquals(testIds.get(2), orderingByID.get(1));
                    assertEquals(testIds.get(1), orderingByID.get(2));
                });
            });
            Utilities.waitForThread(future);
        });
    }

    @Test
    public void similarityOrderTest() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();
                for(int i = 0; i<testPersons.size(); i++) {
                    Utilities.inputBOF(testPersons.get(i), db, userID, "test");
                }

                List<PersonWithCourses> ordering = Utilities.generateClassScoreOrder(db);

                activity.runOnUiThread(() -> {
                    assertEquals(testIds.get(0), ordering.get(0).person.personId);
                    assertEquals(testIds.get(2), ordering.get(1).person.personId);
                    assertEquals(testIds.get(1), ordering.get(2).person.personId);

                    assertEquals(3, ordering.size()); //check that only 3 people had classes in course table
                });

                db.clearAllTables();
                db.close();

                return null;
            });
            Utilities.waitForThread(future);
        });
    }

    @Test
    public void similarityOrderNoInputTest() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();
                List<PersonWithCourses> ordering = Utilities.generateClassScoreOrder(db);

                activity.runOnUiThread(() -> {
                    assertEquals(0, ordering.size()); //check that no one is in the list
                });
                db.clearAllTables();
                db.close();

                return null;
            });
            Utilities.waitForThread(future);
        });
    }
    @Test
    public void removeBOFTest() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();
                int numUserClasses = db.coursesDao().getForPerson(userID).size();

                int courseCount = db.coursesDao().count();

                //only the user's classes entered at this point
                activity.runOnUiThread(() -> {
                    assertEquals(numUserClasses, courseCount);
                });

                for(int i = 0; i<testPersons.size(); i++) {
                    Utilities.inputBOF(testPersons.get(i), db, userID, "test");
                }

                int courseCount2 = db.coursesDao().count();

                //courses of BOFs were added
                activity.runOnUiThread(() -> {
                    assertNotEquals(numUserClasses, courseCount2);
                });

                int personCount = db.personsWithCoursesDao().count();

                //check that people were added correctly
                activity.runOnUiThread(() -> {
                    assertEquals(1 + testPersons.size(), personCount);
                });
                db.coursesDao().deleteBOFs(userID);

                int courseCount3 = db.coursesDao().count();
                int personCount2 = db.personsWithCoursesDao().count();

                activity.runOnUiThread(() -> {
                    //Only the user's classes should remain at this point
                    assertEquals(numUserClasses, courseCount3);

                    //check that people were deleted properly
                    assertEquals(1, personCount2);
                });
                db.clearAllTables();
                db.close();
            });
            Utilities.waitForThread(future);
        });
    }

    @Test
    public void removeBOFWithNoInputTest() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();
                int numUserClasses = db.coursesDao().getForPerson(userID).size();

                int courseCount = db.coursesDao().count();

                //only the user's classes entered
                activity.runOnUiThread(() -> {
                    assertEquals(numUserClasses, courseCount);
                });

                db.coursesDao().deleteBOFs(userID);

                int courseCount2 = db.coursesDao().count();

                //number of classes should not be affected
                activity.runOnUiThread(() -> {
                    assertEquals(numUserClasses, courseCount2);
                });
                db.clearAllTables();
                db.close();
            });
            Utilities.waitForThread(future);
        });
    }



    @Test
    public void test_size_score() {
        assertEquals(.1, Utilities.sizeScore(testPersons.get(0).courses.get(0)), 0.01);
        assertEquals(1, Utilities.sizeScore(testPersons.get(0).courses.get(1)), 0.01);
        assertEquals(.18, Utilities.sizeScore(testPersons.get(0).courses.get(2)), 0.01);
        assertEquals(.06, Utilities.sizeScore(testPersons.get(2).courses.get(0)), 0.01);
        assertEquals(.33, Utilities.sizeScore(testPersons.get(3).courses.get(1)), 0.01);
        assertEquals(.03, Utilities.sizeScore(testPersons.get(3).courses.get(2)), 0.01);
    }

    @Test
    public void test_quarter_to_int_conversion() {
        assertEquals(0, Utilities.quarterToInt("Winter"));
        assertEquals(1, Utilities.quarterToInt("Spring"));
        assertEquals(2, Utilities.quarterToInt("Summer Session I"));
        assertEquals(2, Utilities.quarterToInt("Summer Session II"));
        assertEquals(2, Utilities.quarterToInt("Special Summer Session"));
        assertEquals(2, Utilities.quarterToInt("Summer"));
        assertEquals(3, Utilities.quarterToInt("Fall"));
    }


    @Test
    public void calculate_course_age_test() {
        int[] currentQuarterAndYear = {3, 2022};
        assertEquals(3, testPersons.get(0).courses.get(0).getAge(currentQuarterAndYear));
        assertEquals(5, testPersons.get(1).courses.get(1).getAge(currentQuarterAndYear));
        assertEquals(11, testPersons.get(2).courses.get(0).getAge(currentQuarterAndYear));
        assertEquals(13, testPersons.get(3).courses.get(0).getAge(currentQuarterAndYear));
        assertEquals(29, testPersons.get(3).courses.get(2).getAge(currentQuarterAndYear));
        currentQuarterAndYear[0] = 0;
        currentQuarterAndYear[1] = 2023;
        assertEquals(4, testPersons.get(0).courses.get(0).getAge(currentQuarterAndYear));
        assertEquals(6, testPersons.get(1).courses.get(1).getAge(currentQuarterAndYear));
        assertEquals(12, testPersons.get(2).courses.get(0).getAge(currentQuarterAndYear));
        assertEquals(14, testPersons.get(3).courses.get(0).getAge(currentQuarterAndYear));
        assertEquals(30, testPersons.get(3).courses.get(2).getAge(currentQuarterAndYear));
    }

    @Test
    public void calculate_age_score_test() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        int[] curr = Utilities.getCurrentQuarterAndYear();

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();
                List<List<Course>> similarCourses = generateSimilarCourses();

                activity.runOnUiThread(() -> {
                    assertEquals(15, Utilities.calculateAgeScore(similarCourses.get(0)));
                    assertEquals(5, Utilities.calculateAgeScore(similarCourses.get(1)));
                    assertEquals(10, Utilities.calculateAgeScore(similarCourses.get(2)));
                    assertEquals(0, Utilities.calculateAgeScore(similarCourses.get(3)));
                });

                db.clearAllTables();
                db.close();

            });
            Utilities.waitForThread(future);

        });

    }

    @Test
    public void calculate_size_score_test() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            db = AppDatabase.singleton(getApplicationContext());

            Future future = backgroundThreadExecutor.submit(() -> {
                addUser();
                List<List<Course>> similarCourses = generateSimilarCourses();

                activity.runOnUiThread(() -> {
                    assertEquals(1.28, Utilities.calculateSizeScore(similarCourses.get(0)), 0.01);
                    assertEquals(.18, Utilities.calculateSizeScore(similarCourses.get(1)), 0.01);
                    assertEquals(1.18, Utilities.calculateSizeScore(similarCourses.get(2)), 0.01);
                    assertEquals(0, Utilities.calculateSizeScore(similarCourses.get(3)), 0.01);
                });

                db.clearAllTables();
                db.close();

            });
            Utilities.waitForThread(future);
        });
    }
}
