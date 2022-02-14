package com.example.birdsoffeather.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "courses")
public class Course implements Serializable {
    @PrimaryKey(autoGenerate = true)
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

    public Course(int personId, String year, String quarter, String subject, String number) {
        this.personId = personId;
        this.year = year;
        this.quarter = getFormattedQuarter(quarter);
        this.subject = subject.trim().toUpperCase();
        this.number = number.trim().toUpperCase();
    }

    public String toString(){
        return year + quarter + subject+ number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseId == course.courseId && personId == course.personId && Objects.equals(year, course.year) && Objects.equals(quarter, course.quarter) && Objects.equals(subject, course.subject) && Objects.equals(number, course.number);
    }

    private String getFormattedQuarter(String input) {
        if(input.equals("FA") || input.equals("Fall")){
            return "Fall";
        } else if(input.equals("WI") || input.equals("Winter")){
            return "Winter";
        } else if(input.equals("SP") || input.equals("Spring")){
            return "Spring";
        } else if(input.equals("SSi") || input.equals("Summer Session I")){
            return "Summer Session I";
        } else if(input.equals("SS2") || input.equals("Summer Session II")){
            return "Summer Session II";
        } else if(input.equals("SSS") || input.equals("Special Summer Session")){
            return "Special Summer Session";
        } else {
            return "";
        }
    }
}
