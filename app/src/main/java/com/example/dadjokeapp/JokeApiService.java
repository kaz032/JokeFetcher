package com.example.dadjokeapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface JokeApiService {
    @Headers("Accept: application/json")
    @GET(".")
    Call<Joke> getRandomJoke();
}