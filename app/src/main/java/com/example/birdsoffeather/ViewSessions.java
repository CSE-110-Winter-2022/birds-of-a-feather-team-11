package com.example.birdsoffeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.birdsoffeather.model.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ViewSessions extends AppCompatActivity {

    private AppDatabase db;
    private RecyclerView sessionsRecyclerView;
    private SessionsViewAdapter sessionsViewAdapter;

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sessions);

        Log.i("ViewSessionsActivity", "Activity started");

        // assign database to variable
        db = AppDatabase.singleton(this);

        // associate variable to the "view sessions" recycler view
        sessionsRecyclerView = findViewById(R.id.view_ssn_recyclerview);

        // set layout manager
        RecyclerView.LayoutManager sessionsLayoutManager = new LinearLayoutManager(this);
        sessionsRecyclerView.setLayoutManager(sessionsLayoutManager);

        // set adapter
        sessionsViewAdapter = new SessionsViewAdapter(new ArrayList<>());
        sessionsRecyclerView.setAdapter(sessionsViewAdapter);

        // display saved sessions
        List<String> saved_sessions = db.sessionsDao().getSessionNames();
        sessionsViewAdapter.updateList(saved_sessions);
    }
}