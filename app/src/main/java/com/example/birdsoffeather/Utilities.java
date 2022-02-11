package com.example.birdsoffeather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Utilities {
    public static void showAlert(Activity activity, String message) {
        showAlert(activity, message, (dialog, id) -> {
            dialog.cancel();
        });
    }

    public static void showAlert(Activity activity, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("Ok", onClickListener)
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static byte[] serializePerson(PersonWithCourses person) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);

        out.writeObject(person);
        out.flush();

        byte [] message = bos.toByteArray();

        out.close();
        bos.close();

        return message;
    }

    public static PersonWithCourses deserializePerson(byte [] message) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(message);
        ObjectInputStream in = new ObjectInputStream(bis);

        PersonWithCourses person = (PersonWithCourses) in.readObject();

        in.close();
        bis.close();

        return person;
    }
}
