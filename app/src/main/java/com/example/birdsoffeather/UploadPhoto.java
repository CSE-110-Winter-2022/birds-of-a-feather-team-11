package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class UploadPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        if (preferences.getString("Photo URL", null) != null) {
            Intent intent = new Intent(this, EnterClasses.class);
            startActivity(intent);
            finish();
        }
    }
    public void onSkipButtonClick(View view) {
        submitURL(getResources().getString(R.string.default_photo_url));
    }
    public void onSubmitButtonClick(View view) {
        TextView photoURLView = findViewById(R.id.photo_url);
        String photoURL = photoURLView.getText().toString();
        submitURL(photoURL);
        /*
        boolean isValidImage = false;
        try {
            URLConnection connection = new URL(photoURL).openConnection();
            String contentType = connection.getHeaderField("Content-Type");
            isValidImage = contentType.startsWith("image/");
        } catch (IOException e) {}
        if (isValidImage) {
            submitURL(photoURLView, photoURL);
        } else {
            Utilities.showAlert(this, "Please input a valid image URL!");
            photoURLView.setText("");
        }*/
    }

    public void submitURL(String url) {
        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Photo URL", url);
        editor.apply();
        Intent intent = new Intent(this, EnterClasses.class);
        startActivity(intent);
        finish();
    }
}