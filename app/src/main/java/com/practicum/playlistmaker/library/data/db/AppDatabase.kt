package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.data.db.dao.FavTracksDao
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity

@Database(version = 2, entities = [TrackEntity::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favTracksDao(): FavTracksDao
}