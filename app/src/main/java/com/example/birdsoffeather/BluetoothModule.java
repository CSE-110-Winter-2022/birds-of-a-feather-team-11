package com.example.birdsoffeather;

import android.content.Context;

import com.example.birdsoffeather.model.db.Person;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

public class BluetoothModule {


    public final MessageListener messageListener;
    public final Context context;
    public Message message;
    public BluetoothModule(MessageListener messageListener, Context context) {
        this.messageListener = messageListener;
        this.context = context;
    }

    public void publish(Message message) {
        Nearby.getMessagesClient(context).publish(message);
    }

    public void unpublish() {
        Nearby.getMessagesClient(context).unpublish(message);
    }

    public void subscribe() {
        Nearby.getMessagesClient(context).subscribe(messageListener);
    }

    public void unsubscribe() {
        Nearby.getMessagesClient(context).unsubscribe(messageListener);
    }
}
