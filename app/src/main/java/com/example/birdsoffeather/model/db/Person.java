package com.example.birdsoffeather.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "persons")
public class Person {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int personId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "profile_url")
    public String profile_url;

    public Person(int personId, String name, String profile_url){
        this.personId = personId;
        this.name = name;
        this.profile_url = profile_url;
    }

}