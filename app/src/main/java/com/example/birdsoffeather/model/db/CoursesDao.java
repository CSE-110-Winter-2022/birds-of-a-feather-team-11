package com.example.birdsoffeather.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CoursesDao {
    @Transaction
    @Query("SELECT * FROM courses where person_id=:personId order by id")
    List<Course> getForPerson(int personId);

    //@Query("SELECT * FROM courses WHERE id=:id")
    //Course get(int id);

    @Query("SELECT COUNT(*) FROM courses")
    int count();

    @Insert
    void insert(Course course);

    @Delete
    void delete(Course course);

    @Query("DELETE FROM courses")
    void deleteAll();
}