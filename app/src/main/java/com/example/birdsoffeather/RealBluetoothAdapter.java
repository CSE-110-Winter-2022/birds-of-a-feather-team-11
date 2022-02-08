package com.example.birdsoffeather;

import android.content.Context;

import com.example.birdsoffeather.model.db.Person;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

public class RealBluetoothAdapter{


    public final MessageListener messageListener;
    public final Context context;
    public Message message;
    public RealBluetoothAdapter(MessageListener messageListener, Context context) {
        this.messageListener = messageListener;
        this.context = context;
    }

    public void publish(Person person) {
        message = new Message(person.toString().getBytes());
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
