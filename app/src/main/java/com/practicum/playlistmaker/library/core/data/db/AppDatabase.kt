package com.practicum.playlistmaker.library.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.core.data.db.dao.FavTracksDao
import com.practicum.playlistmaker.library.core.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.library.core.data.db.dao.TracksDao
import com.practicum.playlistmaker.library.core.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.core.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.library.core.data.db.entity.TrackEntity

@Database(
    version = 7,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favTracksDao(): FavTracksDao

    abstract fun playlistsDao(): PlaylistsDao

    abstract fun tracksDao(): TracksDao
}