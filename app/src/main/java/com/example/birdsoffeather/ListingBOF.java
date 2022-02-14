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
import com.example.birdsoffeather.model.db.IPerson;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListingBOF extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;


    private boolean bluetoothStarted;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothModule bluetooth;

    private PersonWithCourses selfPerson;

    private RecyclerView personsRecyclerView;
    private PersonsViewAdapter personsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_bof);

        bluetoothStarted = false;

        // Obtain details of use
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

        // Get updated list of similar classes in background thread and then use ui thread to update UI
        this.future = backgroundThreadExecutor.submit(() -> {
            List<PersonWithCourses> persons = Utilities.generateSimilarityOrder(db);
            runOnUiThread(() -> {
                updateUI(persons);
            });
            return null;
        });

    }


    /**
     * Updates UI to show recycler view, i.e. list of students
     *
     * @param persons - List of persons to show in recycler view
     */
    public void updateUI(List<? extends IPerson> persons) {
        personsViewAdapter.updateList(persons);
    }

    /**
     * Runs the process of setting up Message Listeners and messages so we can use BluetoothModule
     */
    private void setupBluetooth() {
        // Check if phone is bluetooth capable and if enabled
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Utilities.showAlert(this, "Your phone is not Bluetooth capable. You will not be able to use this app.");
            finish();
            Log.w("Bluetooth", "Phone is not bluetooth capable");
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
                            Log.i("Bluetooth",person.toString() + " found");
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
            Log.w("Bluetooth","Bluetooth setup failed");
            e.printStackTrace();
        }
    }

    /**
     * Allows for easy faking and testing of app by passing custom fake listeners
     * @param listener A custom listener can be passed in
     */
    public void setMessageListener(MessageListener listener) {
        bluetooth = new BluetoothModule(this, listener);
    }

    public MessageListener getMessageListener() {
        return bluetooth.messageListener;
    }


    /**
     * Start button changes to Stop button when clicked, and vice versa.
     *
     * When START is clicked, the app will fetch nearby students using the same app who
     * have bluetooth on (via bluetooth), then display the students that have taken the same course
     * as the user.
     * When STOP is clicked, the app will no longer fetch for new students, but the UI will still
     * display the old list of students that were fetched.
     *
     * @param view - Button view
     */
    public void onStartStopClicked(View view) {

        // Stop button from being used if Bluetooth is not enabled
        if (!bluetoothAdapter.isEnabled()) {
            Utilities.showAlert(this,"Don't forget to turn on Bluetooth");
            return;
        }

        Button startStopBtn = findViewById(R.id.start_stop_btn);
        startStopBtn.setSelected(!startStopBtn.isSelected());


        if (bluetoothStarted) {
            // Unpublish and stop Listening
            bluetooth.unpublish();
            bluetooth.unsubscribe();

            // Update State
            startStopBtn.setText("Start");
            bluetoothStarted = false;

        } else {

            // Publish and Listen
            bluetooth.publish();
            bluetooth.subscribe();

            // Update State
            startStopBtn.setText("Stop");
            bluetoothStarted = true;
        }

    }

    public void onAddMockClicked(View view) {
        Intent intent = new Intent(this, NearbyMock.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothStarted) {
            // Unpublish and stop Listening
            bluetooth.unpublish();
            bluetooth.unsubscribe();
        }
    }
}