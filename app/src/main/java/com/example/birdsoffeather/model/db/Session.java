package com.example.birdsoffeather.model.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class Session {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sessionId")
    @NonNull
    public int sessionId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "personId")
    public String personId;

    public Session(String name, String personId) {
        this.name = name;
        this.personId = personId;
    };
}
