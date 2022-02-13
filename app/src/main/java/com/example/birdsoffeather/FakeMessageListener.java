package com.example.birdsoffeather;

import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FakeMessageListener extends MessageListener {
    private final MessageListener messageListener;

    public FakeMessageListener(MessageListener realMessageListener, PersonWithCourses person) {
        this.messageListener = realMessageListener;

        try {
            Message message = new Message(Utilities.serializePerson(person));
            this.messageListener.onFound(message);
            this.messageListener.onLost(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
