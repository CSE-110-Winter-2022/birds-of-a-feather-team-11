package com.example.birdsoffeather.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "persons")
public class Person implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return personId == person.personId && Objects.equals(name, person.name) && Objects.equals(profile_url, person.profile_url);
    }
}