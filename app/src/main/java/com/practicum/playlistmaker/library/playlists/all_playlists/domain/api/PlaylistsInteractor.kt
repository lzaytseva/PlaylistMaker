package com.practicum.playlistmaker.library.playlists.all_playlists.domain.api

import android.net.Uri
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun savePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun saveCoverToStorage(uri: Uri?): Uri

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
}