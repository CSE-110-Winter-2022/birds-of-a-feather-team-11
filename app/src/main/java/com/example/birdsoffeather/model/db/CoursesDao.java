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
    List<Course> getForPerson(String personId);

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

    @Query("SELECT person_id FROM courses WHERE person_id!=:userID GROUP BY person_id ORDER BY COUNT(*) DESC")
    List<String> getSimilarityOrdering(String userID);

    @Query("SELECT COUNT(*) FROM courses WHERE person_id=:userID AND year=:year AND quarter=:quarter AND subject=:subject AND number=:number AND class_size=:classSize")
    int similarCourse(String userID, String year, String quarter, String subject, String number, String classSize);

    @Query("DELETE FROM courses WHERE person_id!=:userID")
    void deleteBOFs(String userID);

}
