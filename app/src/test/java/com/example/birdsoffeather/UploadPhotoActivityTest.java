package com.example.birdsoffeather;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


@RunWith(AndroidJUnit4.class)
public class UploadPhotoActivityTest {

    String defaultPhoto = "https://www.personality-insights.com/wp-content/uploads/2017/12/default-profile-pic-e1513291410505.jpg";
    String profilePic = "https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0";

    @Rule
    public ActivityScenarioRule<UploadPhoto> scenarioRule = new ActivityScenarioRule<>(UploadPhoto.class);

    @Test
    public void test_use_default_profile_photo() {

        ActivityScenario<UploadPhoto> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            Button skipButton = activity.findViewById(R.id.photo_skip);

            skipButton.performClick();
            SharedPreferences preferences = activity.getSharedPreferences("BoF", Context.MODE_PRIVATE);
            assertEquals(defaultPhoto, preferences.getString("Photo URL", null));

        });
    }


    @Test
    public void test_use_invalid_photo() {
        ActivityScenario<UploadPhoto> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            Button submitButton = activity.findViewById(R.id.photo_submit);
            TextView photoURLView = activity.findViewById(R.id.photo_url_edit_text);

            photoURLView.setText("https://www.google.com/");
            submitButton.performClick();

            SharedPreferences preferences = activity.getSharedPreferences("BoF", Context.MODE_PRIVATE);
            assertEquals(null, preferences.getString("Photo URL", null));
        });
    }

}