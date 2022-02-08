package com.example.birdsoffeather;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

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
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Utilities.showAlert(this, "Please turn on your Bluetooth", ((dialogInterface, i) -> {
                Intent intent = new Intent(this, CreateProfile.class);
                startActivity(intent);
                finish();
            }));
        } else {
            Intent intent = new Intent(this, CreateProfile.class);
            startActivity(intent);
            finish();
        }

    }
}