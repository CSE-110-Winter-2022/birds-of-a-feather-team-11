package com.example.birdsoffeather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsoffeather.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearBOFs();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
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

    public void clearBOFs() {
        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());
            db.coursesDao().deleteBOFs();
            db.personsWithCoursesDao().deleteBOFs();
            return null;
        });
    }
}