package com.example.rgrmobiki.data.database

import androidx.room.*
import com.example.rgrmobiki.data.model.WeatherHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherHistoryDao {
    @Query("SELECT * FROM weather_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<WeatherHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherHistory)

    @Query("DELETE FROM weather_history")
    suspend fun deleteAllHistory()

    @Query("SELECT * FROM weather_history WHERE cityName = :cityName ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestByCity(cityName: String): WeatherHistory?
}

