package com.example.dadjokeapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Response;

public class DailyJokeWorker extends Worker {

    private static final String CHANNEL_ID = "daily_joke_channel";
    private static final int NOTIFICATION_ID = 1;

    public DailyJokeWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            JokeApiService service = RetrofitClient.getService();
            Call<Joke> call = service.getRandomJoke();
            Response<Joke> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                Joke joke = response.body();
                String jokeText = joke.getSetup() + (joke.getPunchline().isEmpty() ? "" : " " + joke.getPunchline());
                sendNotification(jokeText);
                return Result.success();
            } else {
                return Result.retry();
            }
        } catch (Exception e) {
            return Result.retry();
        }
    }

    private void sendNotification(String jokeText) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Joke",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Daily Dad Joke")
                .setContentText(jokeText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}