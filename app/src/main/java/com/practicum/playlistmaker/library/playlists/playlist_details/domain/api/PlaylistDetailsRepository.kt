package com.practicum.playlistmaker.library.playlists.playlist_details.domain.api

import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistDetailsRepository {

    suspend fun getPlaylist(playlistId: Int): Playlist

    fun getTracks(tracksId: List<Int>): Flow<List<Track>>
}