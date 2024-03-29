package com.example.birdsoffeather.model.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Person.class, Course.class, Session.class}, version=1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase singletonInstance;

    public static AppDatabase singleton(Context context) {
        if(singletonInstance == null) {
            singletonInstance = Room.databaseBuilder(context, AppDatabase.class, "person.db")
                    .allowMainThreadQueries()
                    .build();
        }
        return singletonInstance;
    }

    public abstract PersonWithCoursesDao personsWithCoursesDao();
    public abstract CoursesDao coursesDao();
    public abstract SessionsDao sessionsDao();
}
