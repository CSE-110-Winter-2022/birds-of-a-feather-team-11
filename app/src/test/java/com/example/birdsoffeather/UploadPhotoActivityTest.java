package com.example.birdsoffeather;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


@RunWith(AndroidJUnit4.class)
public class UploadPhotoActivityTest {

    String defaultPhoto = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fbuildyourspechere.com%2Fcontact-3%2Fattachment%2Fplaceholder-image-person-jpg%2F&psig=AOvVaw3dEQgPH9ClCdcAFY5CTXpw&ust=1643933146971000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCOCgsK2e4vUCFQAAAAAdAAAAABAD";
    String profilePic = "https://www.google.com/imgres?imgurl=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fe%2Feb%2FAsh_Tree_-_geograph.org.uk_-_590710.jpg&imgrefurl=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FTree&tbnid=V_YlBpZ51iMzqM&vet=12ahUKEwj47o6FsO31AhVNATQIHRatAsEQMygAegUIARDUAQ..i&docid=wHCoEH9G9w_hKM&w=480&h=640&itg=1&q=tree&ved=2ahUKEwj47o6FsO31AhVNATQIHRatAsEQMygAegUIARDUAQ";

    @Rule
    public ActivityScenarioRule<UploadPhoto> scenarioRule = new ActivityScenarioRule<>(UploadPhoto.class);

    @Test
    public void test_use_default_profile_photo() {

        ActivityScenario<UploadPhoto> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            Button skipButton = activity.findViewById(R.id.photo_skip);
            TextView photoURLView = activity.findViewById(R.id.photo_url);

            skipButton.performClick();
            assertEquals(defaultPhoto, photoURLView.getText().toString());

        });
    }


    @Test
    public void test_use_inputted_profile_photo() {

        ActivityScenario<UploadPhoto> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            Button submitButton = activity.findViewById(R.id.photo_submit);
            TextView photoURLView = activity.findViewById(R.id.photo_url);
            photoURLView.setText(profilePic);

            submitButton.performClick();
            assertEquals(profilePic, photoURLView.getText().toString());
        });
    }

    /*
    @Test
    public void test_use_invalid_photo() {
        // Create a "scenario" to move through the activity lifecycle.
        // https://developer.android.com/guide/components/activities/activity-lifecycle
        ActivityScenario<UploadPhoto> scenario = scenarioRule.getScenario();

        // Make sure the activity is in the created state (so onCreated is called).
        scenario.moveToState(Lifecycle.State.CREATED);

        // When it's ready, we're ready to test inside this lambda (anonymous inline function).
        scenario.onActivity(activity -> {
            Button submitButton = activity.findViewById(R.id.photo_submit);
            TextView photoURLView = activity.findViewById(R.id.photo_url);

            photoURLView.setText("abcdefg");
            submitButton.performClick();

            assertEquals("", photoURLView.getText().toString());
        });
    }*/

}
