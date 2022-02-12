package com.example.birdsoffeather.model.db;

import java.util.List;

public interface IPerson {
    public int getId();
    public String getName();
    public String getUrl();
    public List<Course> getCourses();
}
