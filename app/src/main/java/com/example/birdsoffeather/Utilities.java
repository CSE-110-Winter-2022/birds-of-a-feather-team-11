package com.example.birdsoffeather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;

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

    /**
     * Serializes the user's information to send over bluetooth
     *
     * @param person user's information to be serialized to stream over bluetooth
     * @return serialized representation of the user's information
     * @throws IOException
     */
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

    /**
     * Deserializes a byte array into an object that represents a user's information
     *
     * @param message serialized representation of a user's information
     * @return Object representation of the user's information
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static PersonWithCourses deserializePerson(byte [] message) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(message);
        ObjectInputStream in = new ObjectInputStream(bis);

        PersonWithCourses person = (PersonWithCourses) in.readObject();

        in.close();
        bis.close();

        return person;
    }

    /**
     * Inputs a potential BOF into the Room database if they have similar courses with the user
     *
     * @param potentialBOF Object representing the other user who may qualify as a BOF
     * @param db Singleton instance to access the Room database
     */
    public static void inputBOF(PersonWithCourses potentialBOF, AppDatabase db, String userID) {
        Person userInfo = potentialBOF.person;
        if (db.personsWithCoursesDao().get(userInfo.personId) != null)
            return;
        Person user = new Person(userInfo.personId, userInfo.name, userInfo.profile_url);
        db.personsWithCoursesDao().insertPerson(user);
        List<Course> courses = potentialBOF.getCourses();
        for (Course course : courses) {
            if (db.coursesDao().similarCourse(userID, course.year, course.quarter, course.subject, course.number) != 0)
                db.coursesDao().insert(new Course(userInfo.personId, course.year, course.quarter, course.subject, course.number));
        }
    }


    /**
     * Generates an ordering of the BOFs based on how many courses they have in common with the user
     *
     * @param db Singleton instance to access the Room database
     * @return list of BOFs in order of how many courses they have in common with the user.
     */
    public static List<PersonWithCourses> generateSimilarityOrder(AppDatabase db, String userID) {
        List<String> orderedIds = db.coursesDao().getSimilarityOrdering(userID);
        List<PersonWithCourses> orderedBOFs = orderedIds.stream().map((id) -> db.personsWithCoursesDao().get(id)).collect(Collectors.toList());
        return orderedBOFs;
    }

    /**
     * Checks if the course entered has already been entered by the user
     *
     * @param newCourse the course just entered by the user
     * @param courses list of courses already entered by the user
     * @return whether or not the course had already been entered by the user
     */
    public static boolean isDuplicate(Course newCourse, List<Course> courses){
        for(Course c: courses)
            if (c.year.equals(newCourse.year) && c.quarter.equals(newCourse.quarter) && c.subject.equals(newCourse.subject) && c.number.equals(newCourse.number))
                return true;
        return false;
    }
}
