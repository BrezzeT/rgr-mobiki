package com.example.rgrmobiki.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    
    /**
     * ВАЖЛИВО: Замініть на ваш API ключ з OpenWeather
     * 
     * Як отримати API ключ:
     * 1. Перейдіть на https://openweathermap.org/api
     * 2. Зареєструйтеся або увійдіть у свій акаунт
     * 3. Перейдіть у розділ "API keys"
     * 4. Створіть новий API ключ (безкоштовний план доступний)
     * 5. Вставте ключ нижче замість "YOUR_API_KEY_HERE"
     */
    const val API_KEY = "YOUR_API_KEY_HERE"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherApiService: WeatherApiService = retrofit.create(WeatherApiService::class.java)
}

