package com.example.birdsoffeather;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.BluetoothMessageComposite;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.IPerson;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListingBOF extends AppCompatActivity {

    private AppDatabase db;
    private SharedPreferences preferences;

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    private boolean bluetoothStarted;

    private BluetoothModule bluetooth;

    private RecyclerView personsRecyclerView;
    private PersonsViewAdapter personsViewAdapter;
    private String userID = null;
    private String sessionName = null;

    private final MessageListener messageListener = new MessageListener() {
        @Override
        public void onFound(@NonNull Message message) {
            super.onFound(message);
            try {
                BluetoothMessageComposite bluetoothMessage = Utilities.deserializeMessage(message.getContent());
                Log.i("Bluetooth","onFound() called");
                Log.i("Bluetooth",bluetoothMessage.person.toString() + " found");
                Log.i("Bluetooth","They sent waves to: " + bluetoothMessage.wavedToUUID);
                backgroundThreadExecutor.submit(() -> {
                    PersonWithCourses potentialBOF = bluetoothMessage.person;
                    Utilities.inputBOF(potentialBOF, db, userID, sessionName);
                    Utilities.updateWaves(db, userID, potentialBOF.getId(), bluetoothMessage.wavedToUUID);
                    List<PersonWithCourses> sortedList = generateSortedList(Utilities.DEFAULT);
                    runOnUiThread(() -> {
                        updateUI(sortedList);
                    });
                    return null;
                });
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onLost(@NonNull Message message) {
            super.onLost(message);
            try {
                BluetoothMessageComposite bluetoothMessage = Utilities.deserializeMessage(message.getContent());
                Log.i("Bluetooth","onLost() called");
                Log.i("Bluetooth",bluetoothMessage.person.toString() + " lost");
                Log.i("Bluetooth","They sent waves to: " + bluetoothMessage.wavedToUUID);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    };
    private TextView title;
    private Button startStopBtn;
    private Button viewSessionBtn;

    private static boolean VIEW_BTN_CLICKED = false;
    private static boolean SHOW_SAVED_SESSION = false;

    /**
     * used to retrieve the session name set by user in StopSave activity.
     * will not execute if user presses back button during StopSave activity.
     */
    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            // update session name in SharedPreferences and title
            if(result != null) {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null && result.getData().getStringExtra(StopSave.KEY_NAME) != null) {
                        Log.i("ListingBOF: onActivityResult", "RESULT_OK");
                        String sn = result.getData().getStringExtra(StopSave.KEY_NAME);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("currentSession", sn);
                        editor.apply();
                        updateCurrentSessionName();
                        updateTitle();

                        // a session was selected from the view sessions page
                        if(VIEW_BTN_CLICKED) {
                            Log.i("ListingBOF: onActivityResult", "A session was selected from the view sessions page.");
                            SHOW_SAVED_SESSION = true;
                            VIEW_BTN_CLICKED = false;
                        }
                    }
                }
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_bof);

        bluetoothStarted = false;

        title = (TextView) findViewById(R.id.bof_title);
        startStopBtn = findViewById(R.id.start_stop_btn);
        viewSessionBtn = findViewById(R.id.view_sessions_btn);

        preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        userID = preferences.getString("userID", null);

        // Obtain details of use
        backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());
            PersonWithCourses person = db.personsWithCoursesDao().get(userID);
            setupBluetooth(person);
            return;
        });

        personsRecyclerView = findViewById(R.id.persons_view);

        // set layout manager
        RecyclerView.LayoutManager personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        // set adapter
        personsViewAdapter = new PersonsViewAdapter(new ArrayList<>(), db);
        personsRecyclerView.setAdapter(personsViewAdapter);

        //set filter spinner
        Spinner filterSpinner = (Spinner) findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.filters_array, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String filter = adapterView.getItemAtPosition(i).toString();
                Log.d("FilterSelect", filter);
                backgroundThreadExecutor.submit(() -> {
                    String sortType = Utilities.DEFAULT;
                    if (filter.equals("Class Size"))
                        sortType = Utilities.CLASS_SIZE;
                    else if (filter.equals("Class Age"))
                        sortType = Utilities.CLASS_AGE;
                    List<PersonWithCourses> persons = generateSortedList(sortType);
                    runOnUiThread(() -> {
                        updateUI(persons);
                    });
                    return null;
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        userID = preferences.getString("userID", null);
        sessionName = preferences.getString("currentSession", null);

        updateTitle();

        // Get updated list of similar classes in background thread and then use ui thread to update UI
        this.future = backgroundThreadExecutor.submit(() -> {
            List<PersonWithCourses> persons = generateSortedList(Utilities.DEFAULT);
            runOnUiThread(() -> {
                updateUI(persons);
            });
            return null;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // update homepage to display the session that was selected in the view sessions page
        if(SHOW_SAVED_SESSION) {
            Log.i("ListingBOF_Activity", "Homepage updated to display selected saved session.");
            showSavedSession();
            SHOW_SAVED_SESSION = false;
        }
    }

    public List<PersonWithCourses> generateSortedList(String sortType) {
        List<PersonWithCourses> orderedList;
        List<PersonWithCourses> toReturn = new ArrayList<>();

        //Get the sorted list with the appropriate sort type
        if (sortType.equals(Utilities.CLASS_SIZE))
            orderedList = Utilities.generateSizeScoreOrder(db);
        else if (sortType.equals(Utilities.CLASS_AGE))
            orderedList = Utilities.generateAgeScoreOrder(db);
        else
            orderedList = Utilities.generateClassScoreOrder(db);

        //Get the list of people in that session
        List<String> sessionUUIDs = db.sessionsDao().getPeopleForSession(sessionName);

        for(PersonWithCourses p : orderedList){
            if(sessionUUIDs.contains(p.person.personId)){
                toReturn.add(p);
            }
        }

        return toReturn;
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
    private void setupBluetooth(PersonWithCourses selfPerson) {
        // Set up bluetooth Module
        bluetooth = new BluetoothModule(messageListener);

        try {
            List<String> sentWaveTo = db.personsWithCoursesDao().getSentWaveTo();
            Message selfMessage = new Message(Utilities.serializeMessage(selfPerson, sentWaveTo));
            bluetooth.setMessage(selfMessage);
        } catch (IOException e) {
            onBluetoothFailed();
            Log.w("Bluetooth","Bluetooth setup failed");
            e.printStackTrace();
        }

    }

    /**
     * Allows for easy faking and testing of app by passing custom fake listeners
     * @param listener A custom listener can be passed in
     */
    public void setMessageListener(MessageListener listener) {
        bluetooth = new BluetoothModule(listener);
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public PersonsViewAdapter getPersonsViewAdapter() { return personsViewAdapter; }


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
        if(bluetooth == null) {
            return;
        }

        startStopBtn.setSelected(!startStopBtn.isSelected());

        SharedPreferences preferences = getSharedPreferences("BoF", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


        if (bluetoothStarted) {
            //when stop is pressed

            // Unpublish and stop Listening
            bluetooth.unpublish(this);
            bluetooth.unsubscribe(this);

            editor.putBoolean("isSessionRunning", false);
            editor.apply();

            // Update State
            startStopBtn.setText("START");
            bluetoothStarted = false;

            // launch StopSave activity
            nameSession();

            // display view sessions button
            viewSessionBtn.setVisibility(View.VISIBLE);

        } else {
            //When start is pressed
            createSession();
            updateUI(new ArrayList<>());

            editor.putBoolean("isSessionRunning", true);
            editor.apply();

            // Publish and Listen
            bluetooth.publish(this);
            bluetooth.subscribe(this);

            // Update State
            startStopBtn.setText("STOP");
            bluetoothStarted = true;

            // hide view sessions button
            viewSessionBtn.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Redirects to add mock screen which can be used for testing
     * @param view - Button view
     */
    public void onAddMockClicked(View view) {
        Intent intent = new Intent(this, NearbyMock.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothStarted) {
            // Unpublish and stop Listening
            bluetooth.unpublish(this);
            bluetooth.unsubscribe(this);
        }
    }

    /**
     * Creates a new session with the current date and time and adds the name to shared preferences
     */
    private void createSession() {
        //Get current time for initial session name
        Calendar c = Calendar.getInstance();
        String formattedDate = (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR);
        String AM_PM = c.get(Calendar.AM_PM) == 0 ? "AM" : "PM";
        String formattedTime = c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + AM_PM;
        String currTime = formattedDate + " " + formattedTime;

        //Store current session name
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentSession", currTime);
        editor.apply();

        sessionName = currTime;

        //Add default user to session
        this.future = backgroundThreadExecutor.submit(() -> {
            Utilities.addToSession(db, sessionName, userID);
            return null;
        });

        //Change Name on title
        updateTitle();
    }

    private void onBluetoothFailed() {
        finish();
    }


    /**
     * update current saved session's name by clicking title
      * @param view
     */
    public void onTitleClicked(View view) {
        if(startStopBtn.getText().toString().equals("START") && !title.getText().toString().equals("BoF")) {
            nameSession();
        }
    }

    /**
     * The activity that's responsible for naming a session will launch.
     * The current session's name will be replaced with the new one in the database.
     */
    private void nameSession() {
        // launch StopSave activity
        Intent intent = new Intent(this, StopSave.class);
        activityLauncher.launch(intent);
    }

    /**
     * Updates title to the current session's name
     */
    private void updateTitle() {
        if(sessionName == null) {
            title.setText("BoF");
        } else {
            title.setText(sessionName);
        }
    }

    /**
     * Updates session name variable
     */
    private void updateCurrentSessionName() {
        String sn = preferences.getString("currentSession", null);
        if(sn != null) {
            sessionName = sn;
        }
    }

    /**
     * When a session is selected from the view sessions activity,
     * the BOF homepage title will be updated to be the selected session's name
     * and the students associated with that session will be shown.
     */
    private void showSavedSession() {
        // first get saved session name
        updateCurrentSessionName();

        // get students associated with session except for user
        List<String> studentsID = db.sessionsDao().getPeopleForSession(sessionName);
        List<PersonWithCourses> students = new ArrayList<>();

        for(String id : studentsID) {
            PersonWithCourses p = db.personsWithCoursesDao().get(id);

            if(!p.getId().equals(userID)) {
                students.add(p);
            }
        }

        // update title
        updateTitle();

        // update recycler view
        updateUI(students);
    }


    public void onViewSessionsClicked(View view) {
        VIEW_BTN_CLICKED = true;
        Intent intent = new Intent(this, ViewSessions.class);
        activityLauncher.launch(intent);
    }

    public void onFavoriteButtonClicked(View view) {

        Intent intent = new Intent(this, FavoriteListing.class);
        startActivity(intent);
    }

}