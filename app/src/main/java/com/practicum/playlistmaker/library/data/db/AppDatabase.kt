package com.practicum.playlistmaker.library.data.db

import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.data.db.dao.FavTracksDao

abstract class AppDatabase : RoomDatabase() {

    abstract fun favTracksDao(): FavTracksDao
}