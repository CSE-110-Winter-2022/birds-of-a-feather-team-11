package com.example.birdsoffeather;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class CreateSessionTest {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    @Rule
    public ActivityScenarioRule<EnterClasses> scenarioRule = new ActivityScenarioRule<>(EnterClasses.class);

    @Test
    public void sampleTest() {
        assertEquals(2, 1+1);
    }
}