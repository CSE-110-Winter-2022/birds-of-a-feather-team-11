package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class UploadPhoto extends AppCompatActivity {

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

    public void onSkipButtonClick(View view) {
        submitURL(getResources().getString(R.string.default_photo_url));
    }
    public void onSubmitButtonClick(View view) {
        EditText photoURLView = findViewById(R.id.photo_url_edit_text);
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
    }
}