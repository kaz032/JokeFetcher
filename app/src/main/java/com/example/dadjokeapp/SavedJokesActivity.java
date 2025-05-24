package com.example.dadjokeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SavedJokesActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DadJokePrefs";
    private static final String KEY_SAVED_JOKES = "saved_jokes";
    private RecyclerView savedJokesRecyclerView;
    private LinearLayout emptyView;
    private JokeAdapter jokeAdapter;
    private List<Joke> jokeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_saved_jokes);

        savedJokesRecyclerView = findViewById(R.id.savedJokesRecyclerView);
        emptyView = findViewById(R.id.emptyView);

        jokeList = new ArrayList<>();
        jokeAdapter = new JokeAdapter(jokeList);
        savedJokesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        savedJokesRecyclerView.setAdapter(jokeAdapter);

        loadSavedJokes();
    }

    private void loadSavedJokes() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONArray savedJokes = getSavedJokes(prefs);
        jokeList.clear();
        try {
            for (int i = 0; i < savedJokes.length(); i++) {
                JSONObject jsonJoke = savedJokes.getJSONObject(i);
                String setup = jsonJoke.getString("setup");
                String punchline = jsonJoke.getString("punchline");
                jokeList.add(new Joke(setup, punchline));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jokeAdapter.notifyDataSetChanged();
        if (jokeList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            savedJokesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            savedJokesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private JSONArray getSavedJokes(SharedPreferences prefs) {
        String savedJokesString = prefs.getString(KEY_SAVED_JOKES, "[]");
        try {
            return new JSONArray(savedJokesString);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }
}