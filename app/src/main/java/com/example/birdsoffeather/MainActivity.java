package com.example.birdsoffeather;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.birdsoffeather.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If Bluetooth capable and Bluetooth off, request to turn on (let them pass even if they deny
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent intent = new Intent(MainActivity.this, CreateProfile.class);
                startActivity(intent);
                finish();
            }).launch(enableBtIntent);

            /*Utilities.showAlert(this, "Please turn on your Bluetooth", ((dialogInterface, i) -> {
                Intent intent = new Intent(this, CreateProfile.class);
                startActivity(intent);
                finish();
            }));*/
        } else {
            Intent intent = new Intent(this, CreateProfile.class);
            startActivity(intent);
            finish();
        }

    }
}