package com.example.birdsoffeather;

import android.content.Context;
import android.util.Log;

import com.example.birdsoffeather.model.db.Person;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

/**
 * Adapter class for Nearby interface to work with our function calls
 */
public class BluetoothModule {

    private static BluetoothModule bluetoothSingleton;

    public final MessageListener messageListener;
    public Message message;
    public BluetoothModule(MessageListener messageListener) {
        this.messageListener = messageListener;
        setBluetoothSingleton(this);
    }

    private static void setBluetoothSingleton(BluetoothModule bluetoothModule) {
        bluetoothSingleton = bluetoothModule;
    }

    public static BluetoothModule getBluetoothSingleton() {
        return bluetoothSingleton;
    }

    /**
     * Setter for message to be broadcast
     * @param message Serialised person object to be broadcast
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * Starts broadcasting message set previously
     * @param context Passes in current activity
     */
    public void publish(Context context) {
        Nearby.getMessagesClient(context).publish(message);
        Log.i("Bluetooth","User's Message Published");
    }

    /**
     * Stops broadcasting message set previously
     * @param context Passes in current activity
     */
    public void unpublish(Context context) {
        Nearby.getMessagesClient(context).unpublish(message);
        Log.i("Bluetooth","User's Message Unpublished");
    }

    /**
     * Sets up the message listener to react to Bluetooth messages
     * @param context Passes in current activity
     */
    public void subscribe(Context context) {
        Nearby.getMessagesClient(context).subscribe(messageListener);
        Log.i("Bluetooth","Listening to Messages");
    }

    /**
     * Stops the message listener from listening to Bluetooth requests
     * @param context Passes in current activity
     */
    public void unsubscribe(Context context) {
        Nearby.getMessagesClient(context).unsubscribe(messageListener);
        Log.i("Bluetooth","Stopped listening for Messages");
    }
}
