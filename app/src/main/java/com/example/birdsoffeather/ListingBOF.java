package com.example.birdsoffeather;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.IOException;
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
                        inputBOF(person);
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

        Button startStopBtn = findViewById(R.id.start_stop_button);

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

    public void inputBOF(PersonWithCourses potentialBOF) {
        Person userInfo = potentialBOF.person;
        int personId = db.personsWithCoursesDao().count();
        Person user = new Person(personId, userInfo.name, userInfo.profile_url);
        db.personsWithCoursesDao().insertPerson(user);
        List<Course> courses = potentialBOF.getCourses();
        for (Course course : courses) {
            if (db.coursesDao().similarCourse(course.year, course.quarter, course.subject, course.number) != 0)
                db.coursesDao().insert(new Course(db.coursesDao().count(), personId, course.year, course.quarter, course.subject, course.number));
        }
        similarityOrder();
    }

    public void similarityOrder() {
        List<Integer> orderedIds = db.coursesDao().getSimilarityOrdering();
        List<PersonWithCourses> orderedBOFs = orderedIds.stream().map((id) -> db.personsWithCoursesDao().get(id)).collect(Collectors.toList());
        //updateUI(orderedBOFs);
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