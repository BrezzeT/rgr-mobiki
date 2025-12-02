package com.example.rgrmobiki.data.repository

import android.util.Log
import com.example.rgrmobiki.data.api.RetrofitClient
import com.example.rgrmobiki.data.api.WeatherApiService
import com.example.rgrmobiki.data.database.WeatherDatabase
import com.example.rgrmobiki.data.database.WeatherHistoryDao
import com.example.rgrmobiki.data.model.WeatherHistory
import com.example.rgrmobiki.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class WeatherRepository(
    private val weatherApiService: WeatherApiService = RetrofitClient.weatherApiService,
    private val weatherHistoryDao: WeatherHistoryDao
) {
    suspend fun getWeatherByCity(cityName: String): Result<WeatherResponse> {
        return try {
            Log.d("WeatherRepository", "Запит погоди для міста: $cityName")
            Log.d("WeatherRepository", "Використовується API_KEY: ${RetrofitClient.API_KEY}")
            
            val response = weatherApiService.getCurrentWeather(
                cityName = cityName,
                apiKey = RetrofitClient.API_KEY
            )
            Log.d("WeatherRepository", "Успішно отримано погоду для: ${response.name}")
            saveToHistory(response)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            Log.e("WeatherRepository", "HTTP помилка: ${e.code()}, повідомлення: ${e.message()}")
            Log.e("WeatherRepository", "Response body: ${e.response()?.errorBody()?.string()}")
            
            val errorMessage = when (e.code()) {
                401 -> "Невірний API ключ. Перевірте ключ на openweathermap.org. Код помилки: 401"
                404 -> "Місто не знайдено. Перевірте правильність назви"
                429 -> "Забагато запитів. Зачекайте трохи"
                else -> "Помилка сервера: ${e.code()}"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Загальна помилка: ${e.message}", e)
            Result.failure(Exception("Помилка: ${e.message ?: "Невідома помилка"}"))
        }
    }

    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val response = weatherApiService.getCurrentWeatherByCoordinates(
                lat = lat,
                lon = lon,
                apiKey = RetrofitClient.API_KEY
            )
            saveToHistory(response)
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                401 -> "Невірний API ключ. Перевірте ключ на openweathermap.org"
                404 -> "Локація не знайдена"
                429 -> "Забагато запитів. Зачекайте трохи"
                else -> "Помилка сервера: ${e.code()}"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(Exception("Помилка: ${e.message ?: "Невідома помилка"}"))
        }
    }

    private suspend fun saveToHistory(weatherResponse: WeatherResponse) {
        val weather = weatherResponse.weather.firstOrNull()
        val history = WeatherHistory(
            cityName = weatherResponse.name,
            temperature = weatherResponse.main.temp,
            description = weather?.description ?: "",
            icon = weather?.icon ?: ""
        )
        weatherHistoryDao.insertWeather(history)
    }

    fun getWeatherHistory(): Flow<List<WeatherHistory>> {
        return weatherHistoryDao.getAllHistory()
    }

    suspend fun clearHistory() {
        weatherHistoryDao.deleteAllHistory()
    }
}

