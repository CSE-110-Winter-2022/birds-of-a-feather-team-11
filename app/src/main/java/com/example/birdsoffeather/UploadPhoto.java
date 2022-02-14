package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UploadPhoto extends AppCompatActivity {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Display previously entered url if activity started using back arrow
        SharedPreferences preferences = getSharedPreferences("BoF",MODE_PRIVATE);
        String storedName = preferences.getString("Photo URL", null);

        if (storedName != null) {
            EditText nameEditText = findViewById(R.id.photo_url_edit_text);
            nameEditText.setText(storedName);
        }
    }

    /**
     * Set the profile picture url to the default url if the skip button is clicked
     *
     * @param view
     */
    public void onSkipButtonClick(View view) {
        submitURL(getResources().getString(R.string.default_photo_url));
    }

    /**
     * Set the inputted url as the profile picture url if the submit button is clicked
     *
     * @param view
     */
    public void onSubmitButtonClick(View view) {
        EditText photoURLView = findViewById(R.id.photo_url_edit_text);
        String photoURL = photoURLView.getText().toString();

        this.future = backgroundThreadExecutor.submit(() -> {
            boolean isValidImage = false;
            try {
                URLConnection connection = new URL(photoURL).openConnection();
                String contentType = connection.getHeaderField("Content-Type");
                isValidImage = contentType.startsWith("image/");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (isValidImage) {
                submitURL(photoURL);
            } else {
                runOnUiThread(() -> {
                    Utilities.showAlert(this, "Please input a valid image URL!");
                    photoURLView.setText("");
                });
            }

            return null;
        });
    }

    /**
     * Adds the given profile picture url to the user's stored information
     *
     * @param url the profile picture url to associate with the user
     */
    public void submitURL(String url) {
        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Photo URL", url);
        editor.apply();
        Log.i("Shared Preferences", "Stored photo_url");
        Intent intent = new Intent(this, EnterClasses.class);
        startActivity(intent);
    }
}