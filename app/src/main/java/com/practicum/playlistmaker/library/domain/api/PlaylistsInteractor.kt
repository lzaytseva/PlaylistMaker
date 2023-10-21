package com.practicum.playlistmaker.library.domain.api

import android.net.Uri
import com.practicum.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun savePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>
    fun saveCoverToStorage(uri: Uri?): Uri
}