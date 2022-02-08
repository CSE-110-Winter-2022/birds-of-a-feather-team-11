package com.example.birdsoffeather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

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
}
