package com.example.birdsoffeather;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.BluetoothMessageComposite;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(AndroidJUnit4.class)
public class BluetoothTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    String userID = UUID.randomUUID().toString();
    List<String> bofIDs = Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString());

    @Rule
    public ActivityScenarioRule<ListingBOF> scenarioRule = new ActivityScenarioRule<>(ListingBOF.class);

    public BluetoothMessageComposite createTestPersonJohn() {
        PersonWithCourses person = new PersonWithCourses();
        person.courses = Arrays.asList(
                new Course(userID, "1999", "WI", "C", "1","Tiny (<40)"),
                new Course(userID, "1999", "FA", "C", "2","Small (40-75)"));
        person.person = new Person(userID,"John","", 0, 0);
        return new BluetoothMessageComposite(person, bofIDs);
    }

    @Test
    public void serializeSameObjectTest() {
        BluetoothMessageComposite person = createTestPersonJohn();
        BluetoothMessageComposite personDeserialized = null;
        try {
            Message message = new Message(Utilities.serializeMessage(person));
            personDeserialized = Utilities.deserializeMessage(message.getContent());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(person, personDeserialized);
    }

    @Test
    public void serializeDifferentObjectTest() {

        BluetoothMessageComposite person1 = createTestPersonJohn();

        // Create different object
        BluetoothMessageComposite person2 = createTestPersonJohn();

        BluetoothMessageComposite serializedPerson = null;
        try {
            Message message = new Message(Utilities.serializeMessage(person1));
            serializedPerson = Utilities.deserializeMessage(message.getContent());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(person2, serializedPerson);

    }

    @Test
    public void serializeDifferentCoursesTest() {

        BluetoothMessageComposite person1 = createTestPersonJohn();

        BluetoothMessageComposite person2 = new BluetoothMessageComposite(new PersonWithCourses(), bofIDs);

        String userID = person1.person.getId();

        person2.person.courses = Arrays.asList(
                new Course(userID, "1999", "WI", "C", "1","Tiny (<40)")
        );
        person2.person.person = new Person(userID,"John","url", 0, 0);

        BluetoothMessageComposite serializedPerson = null;
        try {
            Message message = new Message(Utilities.serializeMessage(person1));
            serializedPerson = Utilities.deserializeMessage(message.getContent());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        assertNotEquals(person2, serializedPerson);
    }

}