package com.example.birdsoffeather;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.IPerson;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ListingBOF extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;


    private boolean running;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothModule bluetooth;

    private PersonWithCourses selfPerson;

    private RecyclerView personsRecyclerView;
    private PersonsViewAdapter personsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_bof);

        running = false;

        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());

            selfPerson = db.personsWithCoursesDao().get(0);

            return null;
        });

        setupBluetooth();

        personsRecyclerView = findViewById(R.id.persons_view);

        // set layout manager
        RecyclerView.LayoutManager personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        // set adapter
        personsViewAdapter = new PersonsViewAdapter(new ArrayList<>());
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.future = backgroundThreadExecutor.submit(() -> {

            updateUI(Utilities.generateSimilarityOrder(db));
            return null;
        });

    }

    public void updateUI(List<? extends IPerson> persons) {
        personsViewAdapter.updateList(persons);
    }


    private void setupBluetooth() {
        // Check if phone is bluetooth capable and if enabled
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Utilities.showAlert(this, "Your phone is not Bluetooth capable. You will not be able to use this app.");
            finish();
            return;
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            }).launch(enableBtIntent);
        }

        // Set up bluetooth Module
        try {
            Message selfMessage = new Message(Utilities.serializePerson(selfPerson));
            bluetooth = new BluetoothModule(this, new MessageListener() {
                @Override
                public void onFound(@NonNull Message message) {
                    try {
                        PersonWithCourses person = Utilities.deserializePerson(message.getContent());
                        future = backgroundThreadExecutor.submit(() -> {
                            Utilities.inputBOF(person, db);
                            updateUI(Utilities.generateSimilarityOrder(db));
                            return null;
                        });
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });
            bluetooth.setMessage(selfMessage);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to setup bluetooth! Try restarting app.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
    }

    public void setMessageListener(MessageListener listener) {
        bluetooth = new BluetoothModule(this, listener);
    }

    public MessageListener getMessageListener() {
        return bluetooth.messageListener;
    }

    public void onStartStopClicked(View view) {

        if (!bluetoothAdapter.isEnabled()) {
            Utilities.showAlert(this,"Don't forget to turn on Bluetooth");
            return;
        }

        Button startStopBtn = findViewById(R.id.start_stop_btn);
        startStopBtn.setSelected(!startStopBtn.isSelected());


        if (running) {
            // Unpublish and stop Listening
            bluetooth.unpublish();
            bluetooth.unsubscribe();

            // Update State
            startStopBtn.setText("Start");
            running = false;

        } else {

            // Publish and Listen
            bluetooth.publish();
            bluetooth.subscribe();

            // Update State
            startStopBtn.setText("Stop");
            running = true;
        }

    }

    public void onAddMockClicked(View view) {
        Intent intent = new Intent(this, NearbyMock.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (running) {
            // Unpublish and stop Listening
            bluetooth.unpublish();
            bluetooth.unsubscribe();
        }
    }
}