package com.practicum.playlistmaker.library.playlists.playlist_details.domain.impl

import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.api.PlaylistDetailsInteractor
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.api.PlaylistDetailsRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistDetailsInteractorImpl(
    private val repository: PlaylistDetailsRepository
): PlaylistDetailsInteractor {

    override suspend fun getPlaylist(playlistId: Int): Playlist {
        return repository.getPlaylist(playlistId)
    }

    override fun getTracks(tracksId: List<Int>): Flow<List<Track>> {
        return repository.getTracks(tracksId)
    }

}