package com.example.birdsoffeather.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface SessionsDao {
    @Transaction
    @Query("SELECT personId FROM sessions where name=:sessionName")
    List<String> getPeopleForSession(String sessionName);

    @Query("SELECT COUNT(*) FROM sessions")
    int count();

    @Insert
    void insert(Session session);

    @Query("UPDATE sessions SET name =:newName WHERE name=:oldName")
    void renameSession(String oldName, String newName);

    @Query("SELECT COUNT(*) FROM sessions WHERE name=:name AND personId=:personId")
    int similarSession(String name, String personId);

    @Query("SELECT DISTINCT name FROM sessions")
    List<String> getSessionNames();
}
