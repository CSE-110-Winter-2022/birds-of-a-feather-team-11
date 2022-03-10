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

    @Query("UPDATE persons SET waved_to=1 WHERE id=:userID")
    void updateSentWaveTo(String userID);

    @Query("SELECT id FROM persons WHERE waved_to!=0")
    List<String> getSentWaveTo();

    @Query("UPDATE persons SET wave_from=1 WHERE id=:bofID")
    void updateWaveFrom(String bofID);

    @Transaction
    @Query("SELECT * FROM persons WHERE size_score!=0 ORDER BY wave_from DESC, size_score DESC")
    List<PersonWithCourses> getSizeScoreOrdering();

    @Transaction
    @Query("SELECT * FROM persons WHERE age_score!=0 ORDER BY wave_from DESC, age_score DESC")
    List<PersonWithCourses> getAgeScoreOrdering();

    @Transaction
    @Query("SELECT * FROM persons WHERE class_score!=0 ORDER BY wave_from DESC, class_score DESC")
    List<PersonWithCourses> getClassScoreOrdering();
}
