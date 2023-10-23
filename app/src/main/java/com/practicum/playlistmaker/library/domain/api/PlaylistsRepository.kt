package com.practicum.playlistmaker.library.domain.api

import android.net.Uri
import com.practicum.playlistmaker.library.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun savePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun saveCoverToStorage(uri: Uri?): Uri

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun addTrack(track: Track)
}