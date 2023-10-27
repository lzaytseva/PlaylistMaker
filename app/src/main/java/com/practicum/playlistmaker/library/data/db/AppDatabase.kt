package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.data.db.dao.FavTracksDao
import com.practicum.playlistmaker.library.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.library.data.db.dao.TracksDao
import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity

@Database(
    version = 5,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favTracksDao(): FavTracksDao

    abstract fun playlistsDao(): PlaylistsDao

    abstract fun tracksDao(): TracksDao
}