package com.example.birdsoffeather;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.Session;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(AndroidJUnit4.class)
public class AddFavoriteTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    String userID = UUID.randomUUID().toString();

    @Rule
    public ActivityScenarioRule<ListingBOF> scenarioRule = new ActivityScenarioRule<>(ListingBOF.class);

    @Test
    public void testAddFavorite() {
        ActivityScenario<ListingBOF> scenario1 = scenarioRule.getScenario();

        scenario1.moveToState(Lifecycle.State.CREATED);
        scenario1.onActivity(activity -> {
            Future future = backgroundThreadExecutor.submit(() -> {
                AppDatabase db = AppDatabase.singleton(getApplicationContext());
                Person testPerson = new Person(userID, "John", "www.google.com", 0, 0, 0);
                db.personsWithCoursesDao().insertPerson(testPerson);
                int favorite = db.personsWithCoursesDao().get(userID).person.favorite;

                activity.runOnUiThread(() -> {
                    assertEquals(0, favorite);
                });

                db.personsWithCoursesDao().addFavorite(userID);
                int favorite2 = db.personsWithCoursesDao().get(userID).person.favorite;

                activity.runOnUiThread(() -> {
                    assertEquals(1, favorite2);
                });

                db.personsWithCoursesDao().removeFavorite(userID);
                int favorite3 = db.personsWithCoursesDao().get(userID).person.favorite;

                activity.runOnUiThread(() -> {
                    assertEquals(0, favorite3);
                });

                return null;
            });

            Utilities.waitForThread(future);

        });
    }



}
