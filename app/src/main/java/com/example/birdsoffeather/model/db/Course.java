package com.example.birdsoffeather.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "courses")
public class Course implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int courseId;

    @ColumnInfo(name = "person_id")
    public int personId;

    @ColumnInfo(name = "year")
    public String year;

    @ColumnInfo(name = "quarter")
    public String quarter;

    @ColumnInfo(name = "subject")
    public String subject;

    @ColumnInfo(name = "number")
    public String number;

    public Course(int courseId, int personId, String year, String quarter, String subject, String number) {
        this.courseId = courseId;
        this.personId = personId;
        this.year = year;
        this.quarter = quarter;
        this.subject = subject;
        this.number = number;
    }

    public String toString(){
        return year + quarter + subject+ number;
    }
}
