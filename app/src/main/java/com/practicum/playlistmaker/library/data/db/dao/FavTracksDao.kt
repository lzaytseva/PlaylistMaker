package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity

interface FavTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrack(track: TrackEntity)

    @Query("SELECT * FROM fav_tracks_table")
    suspend fun getAllTracks(): List<TrackEntity>

    @Delete
    suspend fun deleteTrack(trackId: Int)

    @Query("SELECT trackId FROM fav_tracks_table")
    suspend fun getIds(): List<Int>
}