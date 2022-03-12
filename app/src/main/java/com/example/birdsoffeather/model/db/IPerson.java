package com.example.birdsoffeather.model.db;

import java.util.List;

public interface IPerson {
    public String getId();
    public String getName();
    public String getUrl();
    public List<Course> getCourses();
    public int getWaveFrom();
    boolean sentWaveTo();
    public boolean getFavorite();
}
