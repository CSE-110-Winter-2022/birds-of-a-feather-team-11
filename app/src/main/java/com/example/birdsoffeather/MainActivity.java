package com.example.birdsoffeather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);



        nextActivity(preferences);
    }

    /**
     * Redirects to a new activity depending on how much data of the user is already stored.
     *
     * @param preferences SharedPreferences of the user
     */
    public void nextActivity(SharedPreferences preferences) {
        Intent intent;
        if (preferences.getBoolean("Entered Classes", false))
            intent = new Intent(this, ListingBOF.class);
        else if (preferences.getString("Photo URL", null) != null)
            intent = new Intent(this, EnterClasses.class);
        else if (preferences.getString("Name", null) != null)
            intent = new Intent(this, UploadPhoto.class);
        else
            intent = new Intent(this, GoogleSignInPrompt.class);
        startActivity(intent);
    }
}