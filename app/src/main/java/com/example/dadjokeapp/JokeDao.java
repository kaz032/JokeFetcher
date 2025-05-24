package com.example.dadjokeapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface JokeDao {

    @Insert
    void insert(Joke joke);

    @Query("SELECT * FROM jokes")
    List<Joke> getAllJokes();
}