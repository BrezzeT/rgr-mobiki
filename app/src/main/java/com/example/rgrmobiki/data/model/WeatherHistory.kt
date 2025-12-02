package com.example.rgrmobiki.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "weather_history")
data class WeatherHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cityName: String,
    val temperature: Double,
    val description: String,
    val icon: String,
    val timestamp: Long = System.currentTimeMillis()
)

