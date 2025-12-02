package com.example.rgrmobiki.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rgrmobiki.data.model.WeatherHistory

@Database(entities = [WeatherHistory::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherHistoryDao(): WeatherHistoryDao
}

