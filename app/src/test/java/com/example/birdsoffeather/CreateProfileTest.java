package com.example.birdsoffeather;

import static org.junit.Assert.assertEquals;

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

    }

    @Test
    public void invalidModifiedNameTest() {

    }

}
