package com.example.dadjokeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String PREFS_NAME = "DadJokePrefs";
    private static final String KEY_SAVED_JOKES = "saved_jokes";
    private TextView jokeText;
    private CardView jokeCard;
    private MaterialButton fetchJokeButton;
    private MaterialButton likeButton;
    private MaterialButton viewSavedButton;
    private ImageView volumeIcon;
    private OkHttpClient client;
    private String currentJokeSetup;
    private String currentJokePunchline;
    private boolean isShowingPunchline = false;
    private TextToSpeech textToSpeech;
    private boolean isSpeaking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);

        jokeText = findViewById(R.id.jokeText);
        jokeCard = findViewById(R.id.jokeCard);
        fetchJokeButton = findViewById(R.id.fetchJokeButton);
        likeButton = findViewById(R.id.likeButton);
        viewSavedButton = findViewById(R.id.viewSavedButton);
        volumeIcon = findViewById(R.id.volumeIcon);
        client = new OkHttpClient();

        // Initialize TextToSpeech with Google TTS engine
        textToSpeech = new TextToSpeech(this, this, "com.google.android.tts");

        fetchJokeButton.setOnClickListener(v -> fetchJoke());

        likeButton.setOnClickListener(v -> toggleLike());

        viewSavedButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavedJokesActivity.class);
            startActivity(intent);
        });

        volumeIcon.setOnClickListener(v -> toggleSpeakJoke());

        jokeCard.setOnClickListener(v -> {
            if (currentJokePunchline != null && !currentJokePunchline.isEmpty()) {
                if (!isShowingPunchline) {
                    jokeText.setAlpha(0f);
                    jokeText.setText(currentJokePunchline);
                    jokeText.animate().alpha(1f).setDuration(300).start();
                    isShowingPunchline = true;
                }
            }
        });

        fetchJoke();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "US English language is not supported");
                volumeIcon.setEnabled(false);
                Toast.makeText(MainActivity.this, "Please install US English voice data in TTS settings.", Toast.LENGTH_LONG).show();
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                return;
            }

            Log.d("TTS", "Available voices:");
            Voice selectedVoice = null;
            for (Voice voice : textToSpeech.getVoices()) {
                Log.d("TTS", "Voice: " + voice.getName() + ", Locale: " + voice.getLocale() + ", Quality: " + voice.getQuality());
                if (voice.getLocale().equals(Locale.US)) {
                    String voiceName = voice.getName().toLowerCase();
                    if (voiceName.contains("male") || voiceName.contains("en-us-x-sfg#male") || voiceName.contains("en-us-voice-a")) {
                        selectedVoice = voice;
                        break;
                    }
                }
            }

            if (selectedVoice != null) {
                textToSpeech.setVoice(selectedVoice);
                Log.d("TTS", "Selected voice: " + selectedVoice.getName());
            } else {
                Log.w("TTS", "No male voice found, using default US English voice");
                Toast.makeText(MainActivity.this, "No male voice found. Using default voice.", Toast.LENGTH_SHORT).show();
            }

            textToSpeech.setPitch(0.5f);
            textToSpeech.setSpeechRate(0.8f);
            volumeIcon.setEnabled(true);
        } else {
            Log.e("TTS", "TextToSpeech initialization failed with status: " + status);
            volumeIcon.setEnabled(false);
            Toast.makeText(MainActivity.this, "Text-to-Speech initialization failed.", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchJoke() {
        Request request = new Request.Builder()
                .url("https://icanhazdadjoke.com/")
                .header("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> jokeText.setText("Failed to fetch joke!"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String joke = json.getString("joke");
                        runOnUiThread(() -> {
                            if (joke.contains("?")) {
                                String[] parts = joke.split("\\?", 2);
                                currentJokeSetup = parts[0] + "?";
                                currentJokePunchline = parts[1].trim();
                            } else {
                                currentJokeSetup = joke;
                                currentJokePunchline = "";
                            }
                            jokeText.setText(currentJokeSetup);
                            jokeText.setAlpha(1f);
                            isShowingPunchline = false;
                            stopSpeaking();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> jokeText.setText("Error parsing joke!"));
                    }
                }
            }
        });
    }

    private void toggleLike() {
        if (currentJokeSetup != null && !currentJokeSetup.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            JSONArray savedJokes = getSavedJokes(prefs);
            try {
                JSONObject newJoke = new JSONObject();
                newJoke.put("setup", currentJokeSetup);
                newJoke.put("punchline", currentJokePunchline != null ? currentJokePunchline : "");
                savedJokes.put(newJoke);

                prefs.edit()
                        .putString(KEY_SAVED_JOKES, savedJokes.toString())
                        .apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void toggleSpeakJoke() {
        String jokeToSpeak = isShowingPunchline ? currentJokePunchline : currentJokeSetup;
        if (jokeToSpeak != null && !jokeToSpeak.isEmpty()) {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            } else {
                textToSpeech.speak(jokeToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
    }

    private void stopSpeaking() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
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

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}