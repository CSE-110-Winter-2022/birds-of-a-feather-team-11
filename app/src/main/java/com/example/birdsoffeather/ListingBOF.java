package com.example.birdsoffeather;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.example.birdsoffeather.model.db.IPerson;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.example.birdsoffeather.model.db.Session;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListingBOF extends AppCompatActivity {

    private AppDatabase db;
    private SharedPreferences preferences;

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;


    private boolean bluetoothStarted;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothModule bluetooth;

    private PersonWithCourses selfPerson;

    private RecyclerView personsRecyclerView;
    private PersonsViewAdapter personsViewAdapter;
    private String userID = null;
    private String sessionName = null;

    private TextView title;
    private Button startStopBtn;
    private Button viewSessionBtn;

    private static final int PERMISSIONS_REQUEST_CODE = 1111;

    private static boolean VIEW_BTN_CLICKED = false;
    private static boolean SHOW_SAVED_SESSION = false;

    // used to retrieve the session name set by user in StopSave activity
    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            // update session name in SharedPreferences
            if(result != null) {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null && result.getData().getStringExtra(StopSave.KEY_NAME) != null) {
                        Log.i("ListingBOF: onActivityResult", "RESULT_OK");
                        String sn = result.getData().getStringExtra(StopSave.KEY_NAME);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("currentSession", sn);
                        editor.apply();
                        updateCurrentSessionName();

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
        this.future = backgroundThreadExecutor.submit(() -> {
            db = AppDatabase.singleton(getApplicationContext());

            selfPerson = db.personsWithCoursesDao().get(userID);

            return null;
        });

        if (!havePermissions()) {
            requestPermissions();
        }

        setupBluetooth();

        personsRecyclerView = findViewById(R.id.persons_view);

        // set layout manager
        RecyclerView.LayoutManager personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        // set adapter
        personsViewAdapter = new PersonsViewAdapter(new ArrayList<>());
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
            orderedList = Utilities.generateSimilarityOrder(db, userID);

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
    private void setupBluetooth() {
        // Check if phone is bluetooth capable and if enabled
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Utilities.showAlert(this, "Your phone is not Bluetooth capable. You will not be able to use this app.");
            onBluetoothFailed();
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
                            Utilities.inputBOF(person, db, userID, sessionName);
                            updateUI(generateSortedList(Utilities.DEFAULT));
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
        bluetooth = new BluetoothModule(this, listener);
    }

    public MessageListener getMessageListener() {
        return bluetooth.messageListener;
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

        // Stop button from being used if Bluetooth is not enabled
        if (!bluetoothAdapter.isEnabled()) {
            Utilities.showAlert(this,"Don't forget to turn on Bluetooth");
            return;
        }

        startStopBtn.setSelected(!startStopBtn.isSelected());


        if (bluetoothStarted) {
            //when stop is pressed

            // Unpublish and stop Listening
            bluetooth.unpublish();
            bluetooth.unsubscribe();

            // Update State
            startStopBtn.setText("START");
            bluetoothStarted = false;

            nameSession();
            updateCurrentSessionName();
            updateTitle();

            // display view sessions button
            viewSessionBtn.setVisibility(View.VISIBLE);

        } else {
            //When start is pressed
            createSession();

            // Publish and Listen
            bluetooth.publish();
            bluetooth.subscribe();

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
            bluetooth.unpublish();
            bluetooth.unsubscribe();
        }
    }

    /**
     * Creates a new session with the current date and time and adds the name to shared preferences
     */
    private void createSession() {
        //Get current time for initial session name
        Calendar c = Calendar.getInstance();
        String formattedDate = c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR);
        String AM_PM = c.get(Calendar.AM_PM) == 0 ? "AM" : "PM";
        String formattedTime = c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + AM_PM;
        String currTime = formattedDate + " " + formattedTime;

        //Store current session name
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentSession", currTime);
        editor.apply();

        sessionName = currTime;

        //Add default user to session
        Utilities.addToSession(db, sessionName, userID);

        //Change Name on title
        title.setText(currTime);
    }

    private boolean havePermissions() {
        Log.i("Bluetooth", "Checking if location permissions are granted");
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        Log.i("Bluetooth","Requesting location permissions");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return;
        }
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                Log.i("Bluetooth", "Nearby Permissions denied");
                Toast.makeText(this, "This app needs permission", Toast.LENGTH_SHORT).show();
                onBluetoothFailed();
            } else {
                Log.i("Bluetooth", "Permission granted");
                setupBluetooth();
            }
        }
    }


    private void onBluetoothFailed() {
        finish();
    }


    /**
     * update current saved session's name by clicking title
      * @param view
     */
    public void onTitleClicked(View view) {
        if(startStopBtn.getText().toString().equals("START") && !title.getText().toString().equals("BOF")) {
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

        updateTitle();
    }

    /**
     * Updates title to the current session's name
     */
    private void updateTitle() {
        // update title if it was changed
        title.setText(sessionName);
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

            if(!p.getId().equals(selfPerson.getId())) {
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
}