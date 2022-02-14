package com.example.birdsoffeather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.stream.Collectors;

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

    public static void inputBOF(PersonWithCourses potentialBOF, AppDatabase db) {
        Person userInfo = potentialBOF.person;
        int personId = db.personsWithCoursesDao().count();
        Person user = new Person(personId, userInfo.name, userInfo.profile_url);
        db.personsWithCoursesDao().insertPerson(user);
        List<Course> courses = potentialBOF.getCourses();
        for (Course course : courses) {
            if (db.coursesDao().similarCourse(course.year, course.quarter, course.subject, course.number) != 0)
                db.coursesDao().insert(new Course(personId, course.year, course.quarter, course.subject, course.number));
        }
    }


    public static List<PersonWithCourses> generateSimilarityOrder(AppDatabase db) {
        List<Integer> orderedIds = db.coursesDao().getSimilarityOrdering();
        List<PersonWithCourses> orderedBOFs = orderedIds.stream().map((id) -> db.personsWithCoursesDao().get(id)).collect(Collectors.toList());
        return orderedBOFs;
    }
}
