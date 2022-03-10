package com.example.birdsoffeather;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Session;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class CreateSessionTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    @Rule
    public ActivityScenarioRule<ListingBOF> scenarioRule = new ActivityScenarioRule<>(ListingBOF.class);

    //Unit Tests
    @Test
    public void testSessionInsertion() {
        ActivityScenario<ListingBOF> scenario1 = scenarioRule.getScenario();

        scenario1.moveToState(Lifecycle.State.CREATED);
        scenario1.onActivity(activity -> {
            Future future = backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());

                int numSessions = db.sessionsDao().count();
                activity.runOnUiThread(() -> {
                    assertEquals(0, numSessions);
                });

                db.sessionsDao().insert(new Session("test_session", "test_person"));

                int numSessions2 = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(1, numSessions2);
                });

                List<String> people = db.sessionsDao().getPeopleForSession("test_session");

                activity.runOnUiThread(() -> {
                    assertEquals("test_person", people.get(0));
                    assertEquals(1, people.size());
                });

                List<String> sessions = db.sessionsDao().getSessionNames();

                activity.runOnUiThread(() -> {
                    assertEquals("test_session", sessions.get(0));
                    assertEquals(1, sessions.size());
                });
                return null;
            });

            Utilities.waitForThread(future);

        });
    }

    @Test
    public void testMultipleInsertion() {
        ActivityScenario<ListingBOF> scenario1 = scenarioRule.getScenario();

        scenario1.moveToState(Lifecycle.State.CREATED);
        scenario1.onActivity(activity -> {
            Future future = backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());

                int numSessions = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(0, numSessions);
                });

                db.sessionsDao().insert(new Session("test_session1", "test_person1"));
                db.sessionsDao().insert(new Session("test_session1", "test_person2"));
                db.sessionsDao().insert(new Session("test_session1", "test_person3"));
                db.sessionsDao().insert(new Session("test_session1", "test_person4"));

                db.sessionsDao().insert(new Session("test_session2", "test_person1"));
                db.sessionsDao().insert(new Session("test_session2", "test_person2"));
                db.sessionsDao().insert(new Session("test_session2", "test_person3"));

                int numSessions2 = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(7, numSessions2);
                });

                List<String> expectedPeople1 = new ArrayList<>();
                expectedPeople1.add("test_person1");
                expectedPeople1.add("test_person2");
                expectedPeople1.add("test_person3");
                expectedPeople1.add("test_person4");

                List<String> people1 = db.sessionsDao().getPeopleForSession("test_session1");

                activity.runOnUiThread(() -> {
                    assertTrue(expectedPeople1.size()==people1.size()
                            && expectedPeople1.containsAll(people1)
                            && people1.containsAll(expectedPeople1));
                });

                List<String> expectedPeople2 = new ArrayList<>();
                expectedPeople2.add("test_person1");
                expectedPeople2.add("test_person2");
                expectedPeople2.add("test_person3");

                List<String> people2 = db.sessionsDao().getPeopleForSession("test_session2");

                activity.runOnUiThread(() -> {
                    assertTrue(expectedPeople2.size() == people2.size()
                            && expectedPeople2.containsAll(people2)
                            && people2.containsAll(expectedPeople2));
                });

                List<String> expectedSessions = new ArrayList<>();
                expectedSessions.add("test_session1");
                expectedSessions.add("test_session2");

                List<String> sessions = db.sessionsDao().getSessionNames();

                activity.runOnUiThread(() -> {
                    assertTrue(expectedSessions.size() == sessions.size()
                            && expectedSessions.containsAll(sessions)
                            && sessions.containsAll(expectedSessions));
                });

                return null;
            });

            Utilities.waitForThread(future);

        });
    }

    @Test
    public void testRenameSession() {
        ActivityScenario<ListingBOF> scenario1 = scenarioRule.getScenario();

        scenario1.moveToState(Lifecycle.State.CREATED);
        scenario1.onActivity(activity -> {
            Future future = backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());

                db.sessionsDao().insert(new Session("test_session1", "test_person1"));
                db.sessionsDao().insert(new Session("test_session1", "test_person2"));
                db.sessionsDao().insert(new Session("test_session1", "test_person3"));
                db.sessionsDao().insert(new Session("test_session1", "test_person4"));

                db.sessionsDao().renameSession("test_session1", "session");

                List<String> expectedPeople = new ArrayList<>();
                expectedPeople.add("test_person1");
                expectedPeople.add("test_person2");
                expectedPeople.add("test_person3");
                expectedPeople.add("test_person4");

                List<String> people = db.sessionsDao().getPeopleForSession("session");

                activity.runOnUiThread(() -> {
                    assertTrue(expectedPeople.size() == people.size()
                            && expectedPeople.containsAll(people)
                            && people.containsAll(expectedPeople));
                });

                List<String> expectedSessions = new ArrayList<>();
                expectedSessions.add("session");

                List<String> sessions = db.sessionsDao().getSessionNames();

                activity.runOnUiThread(() -> {
                    assertTrue(expectedSessions.size() == sessions.size()
                            && expectedSessions.containsAll(sessions)
                            && sessions.containsAll(expectedSessions));
                });

                return null;
            });

            Utilities.waitForThread(future);

        });
    }

    @Test
    public void testSessionSimilarity() {
        ActivityScenario<ListingBOF> scenario1 = scenarioRule.getScenario();

        scenario1.moveToState(Lifecycle.State.CREATED);
        scenario1.onActivity(activity -> {
            Future future = backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                db.sessionsDao().insert(new Session("test_session", "test_person"));

                int test1 = db.sessionsDao().similarSession("test_session", "test_person");
                int test2 = db.sessionsDao().similarSession("1test_session", "test_person");
                int test3 = db.sessionsDao().similarSession("test_session", "1test_person");

                activity.runOnUiThread(() -> {
                    assertEquals(1, test1);
                    assertEquals(0, test2);
                    assertEquals(0, test3);
                });

                return null;
            });

            Utilities.waitForThread(future);

        });
    }

    @Test
    public void testNullInsertion() {
        ActivityScenario<ListingBOF> scenario1 = scenarioRule.getScenario();

        scenario1.moveToState(Lifecycle.State.CREATED);
        scenario1.onActivity(activity -> {
            Future future = backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                int initial = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(0, initial);
                });

                db.sessionsDao().insert(new Session(null, "test_person"));
                int nullSession = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(0, nullSession);
                });

                db.sessionsDao().insert(new Session("test_session", null));
                int nullPerson = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(0, nullPerson);
                });

                db.sessionsDao().insert(new Session("test_session", "test_person"));
                int valid = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(1, valid);
                });

                return null;
            });

            Utilities.waitForThread(future);

        });
    }

    @Test
    public void testAddToSession() {
        ActivityScenario<ListingBOF> scenario1 = scenarioRule.getScenario();

        scenario1.moveToState(Lifecycle.State.CREATED);
        scenario1.onActivity(activity -> {
            Future future = backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                int initial = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(0, initial);
                });

                Utilities.addToSession(db, "session", "person");
                int first = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(1, first);
                });

                Utilities.addToSession(db, "session", "person");
                int repeat = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(1, repeat);
                });

                Utilities.addToSession(db, "session2", "person");
                int second = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(2, second);
                });

                Utilities.addToSession(db, null, "person");
                int repeat2 = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(2, repeat2);
                });

                return null;
            });

            Utilities.waitForThread(future);

        });
    }

    //Test BDD scenarios
    @Test
    public void testNewSessionCreation() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            Future future = backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                int initial = db.sessionsDao().count();

                activity.runOnUiThread(() -> {
                    assertEquals(0, initial);
                });

                return null;
            });

            Utilities.waitForThread(future);

            SharedPreferences preferences = activity.getSharedPreferences("BoF", Context.MODE_PRIVATE);
            assertEquals(null, preferences.getString("currentSession", null));

            TextView title = activity.findViewById(R.id.bof_title);

            assertEquals("BOF", title.getText());

            Button startButton = activity.findViewById(R.id.start_stop_btn);

            startButton.performClick();

            assertNotEquals("BOF", title.getText());
        });
    }
}
