package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.PersonWithCourses;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteListing extends AppCompatActivity {
    private RecyclerView personsRecyclerView;
    private PersonsViewAdapter personsViewAdapter;
    private AppDatabase db;
    private RecyclerView.LayoutManager personsLayoutManager;

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_listing);
        personsRecyclerView = findViewById(R.id.favorites_view);


        RecyclerView.LayoutManager personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);
        db = AppDatabase.singleton(getApplicationContext());

        personsViewAdapter = new PersonsViewAdapter(new ArrayList<>(), db);
        personsRecyclerView.setAdapter(personsViewAdapter);

        backgroundThreadExecutor.submit(() -> {

            List<PersonWithCourses> persons = db.personsWithCoursesDao().getFavorites();
            runOnUiThread(()-> {
                personsViewAdapter.updateList(persons);
            });
        });
    }


    public void onGoBackToHomeClicked(View view) {
        finish();
    }
}