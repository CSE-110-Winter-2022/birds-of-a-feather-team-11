package com.example.birdsoffeather.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.birdsoffeather.Utilities;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "courses")
public class Course implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int courseId;

    @ColumnInfo(name = "person_id")
    public String personId;

    @ColumnInfo(name = "year")
    public String year;

    @ColumnInfo(name = "quarter")
    public String quarter;

    @ColumnInfo(name = "subject")
    public String subject;

    @ColumnInfo(name = "number")
    public String number;

    @ColumnInfo(name = "class_size")
    public String classSize;

    public Course(String personId, String year, String quarter, String subject, String number, String classSize) {
        this.personId = personId;
        this.year = year;
        this.quarter = getFormattedQuarter(quarter);
        this.subject = subject.trim().toUpperCase();
        this.number = number.trim().toUpperCase();
        this.classSize = classSize;
    }

    public int getAge(int[] currentQuarterAndYear) {
        int currentQuarter = currentQuarterAndYear[0];
        int currentYear = currentQuarterAndYear[1];

        int courseQuarter = Utilities.quarterToInt(this.quarter);

        return ((currentQuarter - courseQuarter) + (currentYear - Integer.parseInt(year)) * 4) - 1;
    }

    public String toString(){
        return year + quarter + subject + number + classSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseId == course.courseId && personId.equals(course.personId) && Objects.equals(year, course.year) && Objects.equals(quarter, course.quarter) && Objects.equals(subject, course.subject) && Objects.equals(number, course.number) && classSize.equals(course.classSize);
    }

    private String getFormattedQuarter(String input) {
        input = input.toUpperCase();
        if(input.equals("FA") || input.equals("FALL")){
            return "Fall";
        } else if(input.equals("WI") || input.equals("WINTER")){
            return "Winter";
        } else if(input.equals("SP") || input.equals("SPRING")){
            return "Spring";
        } else if(input.equals("SSI") || input.equals("SS1") || input.equals("SUMMER SESSION I") || input.equals("SUMMER SESSION 1")){
            return "Summer Session I";
        } else if(input.equals("SSII") || input.equals("SS2") || input.equals("SUMMER SESSION II") || input.equals("SUMMER SESSION 2")){
            return "Summer Session II";
        } else if(input.equals("SSS") || input.equals("SPECIAL SUMMER SESSION")){
            return "Special Summer Session";
        } else {
            return "";
        }
    }
}
