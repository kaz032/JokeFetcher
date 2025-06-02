package com.example.dadjokeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_saved_jokes);

        savedJokesRecyclerView = findViewById(R.id.savedJokesRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        jokeList = new ArrayList<>();
        jokeAdapter = new JokeAdapter(jokeList);
        savedJokesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        savedJokesRecyclerView.setAdapter(jokeAdapter);

        // Set up swipe-to-delete functionality
        setupSwipeToDelete();

        loadSavedJokes();
    }

    private void loadSavedJokes() {
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
        updateVisibility();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final GradientDrawable background;
            private final Drawable deleteIcon;

            {
                background = new GradientDrawable();
                background.setColor(Color.parseColor("#CC3333")); // Softer red

                float cornerRadius = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

                // Round all 4 corners by default (for full swipe)
                background.setCornerRadii(new float[]{
                        cornerRadius, cornerRadius, // top-left
                        cornerRadius, cornerRadius, // top-right
                        cornerRadius, cornerRadius, // bottom-right
                        cornerRadius, cornerRadius  // bottom-left
                });

                deleteIcon = ContextCompat.getDrawable(SavedJokesActivity.this, android.R.drawable.ic_menu_delete);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Joke removedJoke = jokeList.get(position);

                jokeList.remove(position);
                jokeAdapter.notifyItemRemoved(position);

                updateSavedJokesInPrefs();
                updateVisibility();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;

                if (dX < 0) { // Swiping left
                    int left = itemView.getRight() + (int) dX;
                    int right = itemView.getRight();
                    int top = itemView.getTop();
                    int bottom = itemView.getBottom();

                    background.setBounds(left, top, right, bottom);

                    float cornerRadius = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 16, recyclerView.getResources().getDisplayMetrics());

                    background.setCornerRadii(new float[]{
                            cornerRadius, cornerRadius, // top-left
                            cornerRadius, cornerRadius, // top-right
                            cornerRadius, cornerRadius, // bottom-right
                            cornerRadius, cornerRadius  // bottom-left
                    });

                    background.draw(c);

                    if (deleteIcon != null) {
                        int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                        int iconTop = itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                        int iconRight = itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();

                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        deleteIcon.draw(c);
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(savedJokesRecyclerView);
    }


    private void updateSavedJokesInPrefs() {
        JSONArray updatedJokes = new JSONArray();
        for (Joke joke : jokeList) {
            try {
                JSONObject jsonJoke = new JSONObject();
                jsonJoke.put("setup", joke.getSetup());
                jsonJoke.put("punchline", joke.getPunchline());
                updatedJokes.put(jsonJoke);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        prefs.edit().putString(KEY_SAVED_JOKES, updatedJokes.toString()).apply();
    }

    private void updateVisibility() {
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