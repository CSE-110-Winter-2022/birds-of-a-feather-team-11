package com.example.birdsoffeather;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.birdsoffeather.model.db.AppDatabase;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        clearBOFs();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If Bluetooth capable and Bluetooth off, request to turn on (let them pass even if they deny)
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                nextActivity(preferences);
            }).launch(enableBtIntent);

        } else {
            nextActivity(preferences);
        }
    }

    public void nextActivity(SharedPreferences preferences) {
        Intent intent;
        if (preferences.getBoolean("Entered Classes", false))
            intent = new Intent(this, ListingBOF.class);
        else if (preferences.getString("Photo URL", null) != null)
            intent = new Intent(this, EnterClasses.class);
        else if (preferences.getString("Name", null) != null)
            intent = new Intent(this, UploadPhoto.class);
        else
            intent = new Intent(this, CreateProfile.class);
        startActivity(intent);
        finish();

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