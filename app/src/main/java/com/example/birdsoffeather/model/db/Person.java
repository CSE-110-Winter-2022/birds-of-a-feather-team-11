package com.example.birdsoffeather.model.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "persons")
public class Person implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    public String personId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "profile_url")
    public String profile_url;

    @ColumnInfo(name = "size_score")
    public double sizeScore;

    @ColumnInfo(name = "age_score")
    public int ageScore;

    @ColumnInfo(name = "class_score")
    public int classScore;

    @ColumnInfo(name = "waved_to")
    public int wavedTo;

    @ColumnInfo(name = "wave_from")
    public int waveFrom;

    @ColumnInfo(name = "favorite")
    public int favorite;

    public Person(@NonNull String personId, String name, String profile_url, double sizeScore, int ageScore, int classScore){
        this.personId = personId;
        this.name = name;
        this.profile_url = profile_url;
        this.sizeScore = sizeScore;
        this.ageScore = ageScore;
        this.classScore = classScore;
        this.wavedTo = 0;
        this.waveFrom = 0;
        this.favorite = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return personId.equals(person.personId) && Objects.equals(name, person.name) && Objects.equals(profile_url, person.profile_url)
                && this.sizeScore == person.sizeScore && this.ageScore == person.ageScore && this.classScore == person.classScore
                && this.wavedTo == person.wavedTo && this.waveFrom == person.waveFrom && this.favorite == person.favorite;
    }

    public String toString() {
        return personId + ", " + name + ", " + profile_url + ", " + sizeScore + ", " + ageScore + ", " + classScore + ", " + wavedTo + ", "+ waveFrom+", " + favorite + ", ";
    }
}