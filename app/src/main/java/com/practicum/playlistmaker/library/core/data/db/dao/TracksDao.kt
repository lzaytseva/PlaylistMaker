package com.practicum.playlistmaker.library.core.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.core.data.db.entity.PlaylistTrackEntity

@Dao
interface TracksDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM tracks_from_playlists ORDER BY timestamp")
    suspend fun getAllTracks(): List<PlaylistTrackEntity>

    @Delete
    suspend fun deleteTrack(track: PlaylistTrackEntity)
}