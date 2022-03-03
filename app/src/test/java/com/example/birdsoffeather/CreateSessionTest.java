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
        backgroundThreadExecutor.submit(() -> {
            AppDatabase db = AppDatabase.singleton(getApplicationContext());

            assertEquals(0, db.sessionsDao().count());

            db.sessionsDao().insert(new Session("test_session", "test_person"));

            assertEquals(1, db.sessionsDao().count());

            List<String> people = db.sessionsDao().getPeopleForSession("test_session");
            assertEquals("test_person", people.get(0));
            assertEquals(1, people.size());

            List<String> sessions = db.sessionsDao().getSessionNames();
            assertEquals("test_session", sessions.get(0));
            assertEquals(1, sessions.size());

            db.clearAllTables();
            db.close();
            return null;
        });
    }

    @Test
    public void testMultipleInsertion() {
        backgroundThreadExecutor.submit(() -> {
            AppDatabase db = AppDatabase.singleton(getApplicationContext());

            assertEquals(0, db.sessionsDao().count());

            db.sessionsDao().insert(new Session("test_session1", "test_person1"));
            db.sessionsDao().insert(new Session("test_session1", "test_person2"));
            db.sessionsDao().insert(new Session("test_session1", "test_person3"));
            db.sessionsDao().insert(new Session("test_session1", "test_person4"));

            db.sessionsDao().insert(new Session("test_session2", "test_person1"));
            db.sessionsDao().insert(new Session("test_session2", "test_person2"));
            db.sessionsDao().insert(new Session("test_session2", "test_person3"));

            assertEquals(7, db.sessionsDao().count());

            List<String> expectedPeople1 = new ArrayList<>();
            expectedPeople1.add("test_person1");
            expectedPeople1.add("test_person2");
            expectedPeople1.add("test_person3");
            expectedPeople1.add("test_person4");

            List<String> people1 = db.sessionsDao().getPeopleForSession("test_session1");

            assertTrue(expectedPeople1.size() == people1.size()
                    && expectedPeople1.containsAll(people1)
                    && people1.containsAll(expectedPeople1));

            List<String> expectedPeople2 = new ArrayList<>();
            expectedPeople1.add("test_person1");
            expectedPeople1.add("test_person2");
            expectedPeople1.add("test_person3");

            List<String> people2 = db.sessionsDao().getPeopleForSession("test_session2");

            assertTrue(expectedPeople2.size() == people2.size()
                    && expectedPeople2.containsAll(people2)
                    && people2.containsAll(expectedPeople2));

            List<String> expectedSessions = new ArrayList<>();
            expectedPeople1.add("test_session1");
            expectedPeople1.add("test_session2");

            List<String> sessions = db.sessionsDao().getSessionNames();

            assertTrue(expectedSessions.size() == sessions.size()
                    && expectedSessions.containsAll(sessions)
                    && sessions.containsAll(expectedSessions));

            db.clearAllTables();
            db.close();
            return null;
        });
    }

    @Test
    public void testRenameSession() {
        backgroundThreadExecutor.submit(() -> {
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

            assertTrue(expectedPeople.size() == people.size()
                    && expectedPeople.containsAll(people)
                    && people.containsAll(expectedPeople));

            List<String> expectedSessions = new ArrayList<>();
            expectedPeople.add("session");

            List<String> sessions = db.sessionsDao().getSessionNames();

            assertTrue(expectedSessions.size() == sessions.size()
                    && expectedSessions.containsAll(sessions)
                    && sessions.containsAll(expectedSessions));

            db.clearAllTables();
            db.close();
            return null;
        });
    }

    @Test
    public void testSessionSimilarity() {
        backgroundThreadExecutor.submit(() -> {
            AppDatabase db = AppDatabase.singleton(getApplicationContext());
            db.sessionsDao().insert(new Session("test_session", "test_person"));

            assertEquals(1, db.sessionsDao().similarSession("test_session", "test_person"));
            assertEquals(0, db.sessionsDao().similarSession("1test_session", "test_person"));
            assertEquals(0, db.sessionsDao().similarSession("test_session", "1test_person"));
            db.clearAllTables();
            db.close();
            return null;
        });
    }

    @Test
    public void testNullInsertion() {
        backgroundThreadExecutor.submit(() -> {
            AppDatabase db = AppDatabase.singleton(getApplicationContext());
            assertEquals(0, db.sessionsDao().count());

            db.sessionsDao().insert(new Session(null, "test_person"));
            assertEquals(0, db.sessionsDao().count());

            db.sessionsDao().insert(new Session("test_session", null));
            assertEquals(0, db.sessionsDao().count());

            db.sessionsDao().insert(new Session("test_session", "test_person"));
            assertEquals(1, db.sessionsDao().count());

            db.clearAllTables();
            db.close();
            return null;
        });
    }

    @Test
    public void testAddToSession() {
        backgroundThreadExecutor.submit(() -> {
            AppDatabase db = AppDatabase.singleton(getApplicationContext());
            assertEquals(0, db.sessionsDao().count());

            Utilities.addToSession(db, "session", "person");
            assertEquals(1, db.sessionsDao().count());

            Utilities.addToSession(db, "session", "person");
            assertEquals(1, db.sessionsDao().count());

            Utilities.addToSession(db, "session2", "person");
            assertEquals(2, db.sessionsDao().count());

            Utilities.addToSession(db, null, "person");
            assertEquals(2, db.sessionsDao().count());

            db.clearAllTables();
            db.close();
            return null;
        });
    }

    //Test BDD scenarios
    @Test
    public void testNewSessionCreation() {
        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                assertEquals(0, db.sessionsDao().count());

                db.clearAllTables();
                db.close();
                return null;
            });

            SharedPreferences preferences = activity.getSharedPreferences("BoF", Context.MODE_PRIVATE);
            assertEquals(null, preferences.getString("currentSession", null));

            TextView title = activity.findViewById(R.id.bof_title);

            assertEquals("BOF", title.getText());

            Button startButton = activity.findViewById(R.id.start_stop_btn);

            startButton.performClick();

            backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                assertEquals(1, db.sessionsDao().count());

                db.clearAllTables();
                db.close();
                return null;
            });
         });


    }
}
