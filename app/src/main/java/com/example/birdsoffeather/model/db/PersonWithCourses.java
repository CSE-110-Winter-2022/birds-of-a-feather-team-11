package com.example.birdsoffeather.model.db;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


public class PersonWithCourses implements Serializable, IPerson {
    @Embedded
    public Person person;

    @Relation(parentColumn = "id",
            entityColumn = "person_id",
            entity = Course.class)
    public List<Course> courses;

    public String getId() {
        return this.person.personId;
    }

    public String getName() {
        return this.person.name;
    }

    public String getUrl() { return this.person.profile_url; }

    public List<Course> getCourses() {
        return this.courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonWithCourses person1 = (PersonWithCourses) o;
        return Objects.equals(person, person1.person) && Objects.equals(courses, person1.courses);
    }

    public PersonWithCourses() {}

    public PersonWithCourses(Person person, List<Course> courses) {
        this.person = person;
        this.courses = courses;
    }

}
