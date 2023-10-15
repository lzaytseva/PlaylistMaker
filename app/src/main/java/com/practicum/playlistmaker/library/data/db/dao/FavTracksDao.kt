package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity

@Dao
interface FavTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(track: TrackEntity)

    @Query("SELECT * FROM fav_tracks_table ORDER BY timestamp")
    suspend fun getAllTracks(): List<TrackEntity>

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT trackId FROM fav_tracks_table")
    suspend fun getIds(): List<Int>
}