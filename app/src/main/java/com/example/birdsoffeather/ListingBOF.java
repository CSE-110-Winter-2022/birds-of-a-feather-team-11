package com.example.birdsoffeather;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    private List<PersonWithCourses> orderedBOFs = new ArrayList<>();

    private boolean running;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothModule bluetooth;

    private PersonWithCourses selfPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_bof);

        updateSelf();

        running = false;

        setupBluetooth();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public void setupBluetooth() {
        bluetooth = new BluetoothModule(new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                ByteArrayInputStream bis = new ByteArrayInputStream(message.getContent());
                ObjectInputStream in = null;
                PersonWithCourses personWithCourses = null;

                try {
                    in = new ObjectInputStream(bis);
                    personWithCourses = (PersonWithCourses) in.readObject();
                    inputBOF(personWithCourses);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, this);
    }

    public void onStartStopClicked(View view) {

        if (bluetoothAdapter == null) {
            return;
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            }).launch(enableBtIntent);
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

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            try {
                // Deserialize
                out = new ObjectOutputStream(bos);
                out.writeObject(selfPerson);
                out.flush();
                Message selfMessage = new Message(bos.toByteArray());

                // Publish and Listen
                bluetooth.publish(selfMessage);
                bluetooth.subscribe();

                // Update State
                startStopBtn.setText("Stop");
                running = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }

        }

    }


    public void inputBOF(PersonWithCourses potentialBOF) {
        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());
            Person userInfo = potentialBOF.person;
            int personId = db.personsWithCoursesDao().count();
            Person user = new Person(personId, userInfo.name, userInfo.profile_url);
            db.personsWithCoursesDao().insertPerson(user);
            List<Course> courses = potentialBOF.getCourses();
            for (Course course : courses) {
                if (db.coursesDao().similarCourse(course.year, course.quarter, course.subject, course.number) != 0)
                    db.coursesDao().insert(new Course(db.coursesDao().count(), personId, course.year, course.quarter, course.subject, course.number));
            }
            return null;
        });

        // call function to re-render UI
    }

    public void updateSelf() {
        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());
            selfPerson = db.personsWithCoursesDao().get(0);
            return null;
        });
    }

    public void similarityOrder() {
        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());
            List<Integer> orderedIds = db.coursesDao().getSimilarityOrdering();
            orderedBOFs = orderedIds.stream().map((id) -> db.personsWithCoursesDao().get(id)).collect(Collectors.toList());
            return null;
        });
    }
}