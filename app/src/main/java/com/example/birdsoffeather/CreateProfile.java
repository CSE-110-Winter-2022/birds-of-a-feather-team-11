package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class CreateProfile extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // retrieve name from google account
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            EditText name_et = findViewById(R.id.name_edit_text);
            name_et.setText(personName);
        }


        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        if (preferences.getString("Name", null) != null) {
            Intent intent = new Intent(this, UploadPhoto.class);
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

            Intent intent = new Intent(this, UploadPhoto.class);
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