package com.example.birdsoffeather;

import com.example.birdsoffeather.model.db.BluetoothMessageComposite;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.util.List;

public class FakeMessageListener extends MessageListener {
    private final MessageListener messageListener;

    public FakeMessageListener(MessageListener realMessageListener, PersonWithCourses person, List<String> wavedToUUID) {
        this.messageListener = realMessageListener;

        try {
            Message message = new Message(Utilities.serializeMessage(person, wavedToUUID));
            this.messageListener.onFound(message);
            this.messageListener.onLost(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
