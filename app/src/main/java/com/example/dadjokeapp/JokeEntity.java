package com.example.dadjokeapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "jokes")
public class JokeEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String setup;
    private String punchline;

    public JokeEntity(@NonNull String id, String setup, String punchline) {
        this.id = id;
        this.setup = setup;
        this.punchline = punchline;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getSetup() {
        return setup;
    }

    public String getPunchline() {
        return punchline;
    }
}