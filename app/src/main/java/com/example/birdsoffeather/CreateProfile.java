package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CreateProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        if (preferences.getString("Name", null) != null) {
            Intent intent = new Intent(this, EnterClasses.class);
            startActivity(intent);
        }
    }

    public void onClickConfirm(View view) {

        EditText nameEditText = findViewById(R.id.name_edit_text);
        String enteredName = nameEditText.getText().toString();

        if (isValidName(enteredName)) {

            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Name", enteredName);
            editor.apply();

            Intent intent = new Intent(this, EnterClasses.class);
            startActivity(intent);
        } else {
            Utilities.showAlert(this,"Please enter a valid name");
        }

    }

    public static boolean isValidName(String name) {
        if (name == null || name.length() == 0) {
            return false;
        }
        return true;
    }
}