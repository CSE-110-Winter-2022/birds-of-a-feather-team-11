package com.example.birdsoffeather.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface PersonWithCoursesDao {
    @Transaction
    @Query("SELECT * FROM persons")
    List<PersonWithCourses> getAll();

    @Transaction
    @Query("SELECT * FROM persons WHERE id=:id")
    PersonWithCourses get(String id);

    @Query("SELECT COUNT(*) FROM persons")
    int count();

    @Insert
    void insertPerson(Person person);

    @Query("DELETE FROM persons")
    void deleteAll();

    @Query("DELETE FROM persons WHERE id!=:userID")
    void deleteBOFs(String userID);
}
