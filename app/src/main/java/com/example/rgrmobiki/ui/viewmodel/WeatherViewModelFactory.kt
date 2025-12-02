package com.example.rgrmobiki.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.rgrmobiki.data.database.WeatherDatabase
import com.example.rgrmobiki.data.repository.WeatherRepository

class WeatherViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            val database = Room.databaseBuilder(
                context,
                WeatherDatabase::class.java,
                "weather_database"
            ).build()

            val repository = WeatherRepository(
                weatherHistoryDao = database.weatherHistoryDao()
            )

            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

