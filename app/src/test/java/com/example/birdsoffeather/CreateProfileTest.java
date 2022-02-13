package com.example.birdsoffeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateProfileTest {

    @Rule
    public ActivityScenarioRule<CreateProfile> scenarioRule = new ActivityScenarioRule<>(CreateProfile.class);

    @Test
    public void testValidName() {

        assertEquals(true, CreateProfile.isValidName("John"));
        assertEquals(false, CreateProfile.isValidName(null));
        assertEquals(false, CreateProfile.isValidName(""));
    }

    @Test
    public void modifyNameTest() {

        ActivityScenario<CreateProfile> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            EditText nameEditText = activity.findViewById(R.id.name_edit_text);
            nameEditText.setText("John");

            Button button = activity.findViewById(R.id.confirm_button);
            button.performClick();

            SharedPreferences preferences = activity.getSharedPreferences("BoF", Context.MODE_PRIVATE);
            assertEquals("John", preferences.getString("Name", null));
        });
    }


}