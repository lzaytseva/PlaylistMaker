package com.practicum.playlistmaker.library.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.library.core.data.db.entity.PlaylistEntity

@Dao
interface PlaylistsDao {

    @Insert
    suspend fun savePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Update
    suspend fun updateTracksInPlaylist(playlistEntity: PlaylistEntity)
}