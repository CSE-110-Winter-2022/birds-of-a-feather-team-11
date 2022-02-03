package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class UploadPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
    }
    public void onSkipButtonClick(View view) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Photo URL", "@string/default_photo_url");
        editor.apply();
    }
    public void onSubmitButtonClick(View view) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        TextView photoURLView = findViewById(R.id.photo_url);
        editor.putString("Photo URL", photoURLView.getText().toString());
        editor.apply();
    }
}