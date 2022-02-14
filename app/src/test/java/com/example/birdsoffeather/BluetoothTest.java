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
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class BluetoothTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    @Rule
    public ActivityScenarioRule<ListingBOF> scenarioRule = new ActivityScenarioRule<>(ListingBOF.class);

    public static PersonWithCourses createTestPersonJohn() {
        PersonWithCourses person = new PersonWithCourses();
        person.courses = Arrays.asList(
                new Course(1, 0, "1999", "WI", "C", "1"),
                new Course(1, 0, "1999", "FA", "C", "2"));
        person.person = new Person(0,"John","url");
        return person;
    }

    @Test
    public void serializeTest() {

        PersonWithCourses person = createTestPersonJohn();

        PersonWithCourses personCopy = null;
        try {
            Message message = new Message(Utilities.serializePerson(person));
            personCopy = Utilities.deserializePerson(message.getContent());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(person, personCopy);

    }

    @Test
    public void serializeDifferentObjectTest() {

        PersonWithCourses person1 = createTestPersonJohn();

        // Create different object
        PersonWithCourses person2 = createTestPersonJohn();

        PersonWithCourses serializedPerson = null;
        try {
            Message message = new Message(Utilities.serializePerson(person1));
            serializedPerson = Utilities.deserializePerson(message.getContent());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(person2, serializedPerson);

    }

    @Test
    public void serializeDifferentCoursesTest() {

        PersonWithCourses person1 = createTestPersonJohn();

        PersonWithCourses person2 = new PersonWithCourses();
        person2.courses = Arrays.asList(
                new Course(1, 0, "1999", "WI", "C", "1")
        );
        person2.person = new Person(0,"John","url");

        PersonWithCourses serializedPerson = null;
        try {
            Message message = new Message(Utilities.serializePerson(person1));
            serializedPerson = Utilities.deserializePerson(message.getContent());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        assertNotEquals(person2, serializedPerson);
    }

    @Test
    public void testFakePersonSend() {

        ActivityScenario<ListingBOF> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            AppDatabase db = AppDatabase.singleton(getApplicationContext());

            PersonWithCourses fakePerson = new PersonWithCourses();
            fakePerson.person = new Person(0, "John", "www.google.com");
            fakePerson.courses = Arrays.asList(
                    new Course(0, 0, "2022", "Winter", "CSE", "110"));
            MessageListener fake = new FakeMessageListener(activity.getMessageListener(), fakePerson);
            activity.setMessageListener(fake);

            backgroundThreadExecutor.submit(() -> {
                int numPeople = db.personsWithCoursesDao().count();
                int numCourses = db.coursesDao().count();

                assertEquals(1, numPeople);
                assertEquals(1, numCourses);

                String personName = db.personsWithCoursesDao().get(0).person.name;

                assertEquals("John", personName);

                db.close();

                return null;
            });
        });
    }

}