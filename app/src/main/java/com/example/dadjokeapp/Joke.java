package com.example.dadjokeapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "jokes")
public class Joke {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "setup")
    private String setup;

    @ColumnInfo(name = "punchline")
    private String punchline;

    // No-argument constructor for Room
    public Joke() {
        this.setup = "";
        this.punchline = "";
    }

    // Constructor for creating a new Joke with setup and punchline
    public Joke(String setup, String punchline) {
        this.setup = setup;
        this.punchline = punchline;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSetup() {
        return setup;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }

    public String getPunchline() {
        return punchline;
    }

    public void setPunchline(String punchline) {
        this.punchline = punchline;
    }
}