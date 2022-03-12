package com.example.birdsoffeather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.birdsoffeather.model.db.AppDatabase;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);

        clearBOFs(preferences.getString("userID", "default"));


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

    /**
     * Removes all of the previously generated BOFs from the database
     */
    public void clearBOFs(String userID) {
        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());
            db.coursesDao().deleteBOFs(userID);
            db.personsWithCoursesDao().deleteBOFs(userID);
            return null;
        });
    }
}