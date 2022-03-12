package com.example.birdsoffeather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.BluetoothMessageComposite;
import com.example.birdsoffeather.model.db.Course;
import com.example.birdsoffeather.model.db.Person;
import com.example.birdsoffeather.model.db.PersonWithCourses;
import com.example.birdsoffeather.model.db.Session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Utilities {
    public static final String DEFAULT = "Default";
    public static final String CLASS_SIZE = "Class Size";
    public static final String CLASS_AGE = "Class Age";

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
     * @param messageInfo user's information to be serialized to stream over bluetooth
     * @return serialized representation of the user's information
     * @throws IOException
     */
    public static byte[] serializeMessage(BluetoothMessageComposite messageInfo) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);

        out.writeObject(messageInfo);
        out.flush();

        byte [] message = bos.toByteArray();

        out.close();
        bos.close();

        return message;
    }

    public static byte[] serializeMessage(PersonWithCourses person, List<String> wavedToUUID) throws IOException {
        return serializeMessage(new BluetoothMessageComposite(person, wavedToUUID));
    }

    /**
     * Deserializes a byte array into an object that represents a user's information
     *
     * @param message serialized representation of a user's information
     * @return Object representation of the user's information
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static BluetoothMessageComposite deserializeMessage(byte [] message) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(message);
        ObjectInputStream in = new ObjectInputStream(bis);

        BluetoothMessageComposite messageInfo = (BluetoothMessageComposite) in.readObject();

        in.close();
        bis.close();

        return messageInfo;
    }

    /**
     * Inputs a potential BOF into the Room database if they have similar courses with the user
     *
     * @param potentialBOF Object representing the other user who may qualify as a BOF
     * @param db Singleton instance to access the Room database
     */
    public static void inputBOF(PersonWithCourses potentialBOF, AppDatabase db, String userID, String sessionName) {
        Person userInfo = potentialBOF.person;
        addToSession(db, sessionName, userInfo.personId);
        if (db.personsWithCoursesDao().get(userInfo.personId) != null)
            return;
        List<Course> similarCourses = generateSimilarCourses(potentialBOF, db, userID);
        double sizeScore = calculateSizeScore(similarCourses);
        int ageScore = calculateAgeScore(similarCourses);
        int classScore = similarCourses.size();
        Person user = new Person(userInfo.personId, userInfo.name, userInfo.profile_url, sizeScore, ageScore, classScore);
        db.personsWithCoursesDao().insertPerson(user);
        for (Course course : similarCourses)
            db.coursesDao().insert(course);
    }

    /**
     * Returns list of courses that the potential BoF has in common with the user
     *
     * @param potentialBOF Different app user who has connected with current user by bluetooth
     * @param db database
     * @param userID ID of the current user
     * @return
     */
    public static List<Course> generateSimilarCourses(PersonWithCourses potentialBOF, AppDatabase db, String userID) {
        List<Course> courses = potentialBOF.getCourses(), similarCourses = new ArrayList<>();
        for (Course course : courses) {
            Course similarCourse = getSimilarCourse(db, userID, course);
            if (similarCourse != null)
                similarCourses.add(new Course(course.personId, course.year, course.quarter, course.subject, course.number, similarCourse.classSize));
        }
        return similarCourses;
    }

    /**
     * Returns a course if the user has taken the same course as the inputted course
     *
     * @param db database
     * @param userID ID of the user
     * @param course course in question
     * @return The course that the user has taken that matches the input course
     */
    public static Course getSimilarCourse(AppDatabase db, String userID, Course course) {
        List<Course> similarCourse = db.coursesDao().getSimilarCourse(userID, course.year, course.quarter, course.subject, course.number);
        if (similarCourse.size() != 0)
            return similarCourse.get(0);
        return null;
    }

    /**
     * Generates a size score for the potential BOF based off of the classes they have in common with the user
     * @param similarCourses courses that the potential BOF has in common with the user
     * @return cumulative size course for their similar courses
     */
    public static double calculateSizeScore(List<Course> similarCourses) {
        double sum = 0;
        for (Course course : similarCourses)
            sum += sizeScore(course);
        return sum;
    }

    /**
     * Converts the size of a class into a score
     * @param course course we want the score of
     * @return size score of the course
     */
    public static double sizeScore(Course course) {
        switch (course.classSize) {
            case Course.tinyClass:
                return 1;
            case Course.smallClass:
                return .33;
            case Course.mediumClass:
                return .18;
            case Course.largeClass:
                return .1;
            case Course.hugeClass:
                return .06;
            case Course.giganticClass:
                return .03;
        }
        return .03;
    }

    /**
     * Calculates an age score for a potential BOF based on their similar classes with the user
     * @param similarCourses list of courses that the bof has similar with the user
     * @return cumulative age score of their similar course
     */
    public static int calculateAgeScore(List<Course> similarCourses) {
        int sum = 0;
        for (Course course : similarCourses)
            sum += ageScore(course);
        return sum;
    }

    /**
     * Calculates a score for the course based off of the course age
     *
     * @param course course to calculate score of
     * @return age score of the course
     */
    public static int ageScore(Course course) {
        switch (course.getAge(getCurrentQuarterAndYear())) {
            case 0:
                return 5;
            case 1:
                return 4;
            case 2:
                return 3;
            case 3:
                return 2;
        }
        return 1;
    }

    /**
     * Retrieve the current quarter and year based off the current date and time
     * @return current quarter and year
     */
    public static int[] getCurrentQuarterAndYear() {
        int[] quarterAndYear = new int[2];
        Calendar currentTime = Calendar.getInstance();

        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH);
        int day = currentTime.get(Calendar.DAY_OF_MONTH);
        String quarter;

        if (month < 3 || month == 3 && day <= 20)
            quarter = "Winter";
        else if (month < 6 || month == 6 && day <= 13)
            quarter = "Spring";
        else if (month < 10)
            quarter = "Summer";
        else
            quarter = "Fall";

        quarterAndYear[0] = quarterToInt(quarter);
        quarterAndYear[1] = year;
        return quarterAndYear;
    }

    /**
     * Converts the academic quarter to an integer representation
     *
     * @param quarter quarter to be converted
     * @return integer representation of the quarter
     */
    public static int quarterToInt(String quarter) {
        switch (quarter) {
            case "Winter":
                return 0;
            case "Spring":
                return 1;
            case "Fall":
                return 3;
            case "Summer Session I":
            case "Summer Session II":
            case "Special Summer Session":
            case "Summer":
                return 2;
        }
        return -1;
    }

    /**
     * Associates an inputted BoF with the session they were inserted in
     *
     * @param db Singleton instance to access the Room database
     * @param sessionName Name of the session they were inputted under
     * @param personId ID of the BoF being inputted
     */
    public static void addToSession(AppDatabase db, String sessionName, String personId){
        if(sessionName == null || personId == null) {
            return;
        }
        if(db.sessionsDao().similarSession(sessionName, personId) == 0){
            db.sessionsDao().insert(new Session(sessionName, personId));
        }
    }

    /**
     * Orders the BoFs based off of a class size score
     */
    public static List<PersonWithCourses> generateSizeScoreOrder(AppDatabase db) {
        return db.personsWithCoursesDao().getSizeScoreOrdering();
    }

    /**
     * Orders the BoFs based off of a class age score
     */
    public static List<PersonWithCourses> generateAgeScoreOrder(AppDatabase db) {
        return db.personsWithCoursesDao().getAgeScoreOrdering();
    }

    /**
     * Orders the BoFs based off of a class similarity score
     */
    public static List<PersonWithCourses> generateClassScoreOrder(AppDatabase db) {
        return db.personsWithCoursesDao().getClassScoreOrdering();
    }

    /**
     * Updates the waver in the database if they waved to the current user
     *
     * @param db
     * @param userID ID of the current user
     * @param bofID ID of the waver
     * @param wavers List of people the waver waved to
     */
    public static void updateWaves(AppDatabase db, String userID, String bofID, List<String> wavers) {
        if (wavers.contains(userID))
            db.personsWithCoursesDao().updateWaveFrom(bofID);
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

    /**
     * Helper function that will wait for a thread to finish before returning
     *
     * @param future a future associated with a thread that indicates the threads status
     */
    public static void waitForThread(Future future) {
        while(!future.isDone())
            continue;
    }
}
