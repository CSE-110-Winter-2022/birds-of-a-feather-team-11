package com.example.birdsoffeather.model.db;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PersonWithCourses{
    @Embedded
    public Person person;

    @Relation(parentColumn = "id",
            entityColumn = "person_id",
            entity = Course.class)
    public List<Course> courses;

    public int getId() {
        return this.person.personId;
    }

    public String getName() {
        return this.person.name;
    }

    public List<Course> getCourses() {
        return this.courses;
    }
}
