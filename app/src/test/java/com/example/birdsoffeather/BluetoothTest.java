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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class BluetoothTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    String userID = UUID.randomUUID().toString();

    @Rule
    public ActivityScenarioRule<ListingBOF> scenarioRule = new ActivityScenarioRule<>(ListingBOF.class);

    public PersonWithCourses createTestPersonJohn() {
        PersonWithCourses person = new PersonWithCourses();
        person.courses = Arrays.asList(
                new Course(userID, "1999", "WI", "C", "1","Tiny (<40)"),
                new Course(userID, "1999", "FA", "C", "2","Small (40-75)"));
        person.person = new Person(userID,"John","", 0, 0, 0);
        return person;
    }

    @Test
    public void serializeSameObjectTest() {
        PersonWithCourses person = createTestPersonJohn();
        PersonWithCourses personCopy = null;
        try {
            Message message = new Message(Utilities.serializeMessage(person, new ArrayList<>()));
            personCopy = Utilities.deserializeMessage(message.getContent()).person;
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
            Message message = new Message(Utilities.serializeMessage(person1, new ArrayList<>()));
            serializedPerson = Utilities.deserializeMessage(message.getContent()).person;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(person2, serializedPerson);

    }

    @Test
    public void serializeDifferentCoursesTest() {

        PersonWithCourses person1 = createTestPersonJohn();

        PersonWithCourses person2 = new PersonWithCourses();

        String userID = person1.getId();

        person2.courses = Arrays.asList(
                new Course(userID, "1999", "WI", "C", "1","Tiny (<40)")
        );
        person2.person = new Person(userID,"John","url", 0, 0, 0);

        PersonWithCourses serializedPerson = null;
        try {
            Message message = new Message(Utilities.serializeMessage(person1, new ArrayList<>()));
            serializedPerson = Utilities.deserializeMessage(message.getContent()).person;
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

            String userID = UUID.randomUUID().toString();

            PersonWithCourses fakePerson = new PersonWithCourses();
            fakePerson.person = new Person(userID, "John", "www.google.com", 0, 0, 0);
            fakePerson.courses = Arrays.asList(
                    new Course(userID, "2022", "Winter", "CSE", "110","Large (150-250)"));

            MessageListener fake = new FakeMessageListener(activity.getMessageListener(), fakePerson, new ArrayList<>());
            activity.setMessageListener(fake);

            backgroundThreadExecutor.submit(() -> {
                int numPeople = db.personsWithCoursesDao().count();
                int numCourses = db.coursesDao().count();

                assertEquals(1, numPeople);
                assertEquals(1, numCourses);

                String personName = db.personsWithCoursesDao().get(userID).person.name;

                assertEquals("John", personName);

                db.close();

                return null;
            });
        });
    }

}