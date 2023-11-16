package com.practicum.playlistmaker.library.core.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.practicum.playlistmaker.library.core.data.db.entity.PlaylistEntity

@Dao
interface PlaylistsDao {

    @Upsert
    suspend fun savePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlists_table where id = :playlistId")
    suspend fun getPlaylist(playlistId: Int): PlaylistEntity

    @Delete
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)
}