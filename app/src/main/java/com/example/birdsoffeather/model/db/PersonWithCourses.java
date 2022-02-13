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

    public String getUrl() { return this.person.profile_url; }

    public List<Course> getCourses() {
        return this.courses;
    }

    public PersonWithCourses() {}

    public PersonWithCourses(Person person, List<Course> courses) {
        this.person = person;
        this.courses = courses;
    }

}
