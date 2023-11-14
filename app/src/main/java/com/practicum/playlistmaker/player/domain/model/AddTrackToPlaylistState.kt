package com.practicum.playlistmaker.player.domain.model

import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist

sealed interface AddTrackToPlaylistState {
    data class WasAdded(
        val playlistName: String,
        val feedbackWasShown: Boolean
    ) : AddTrackToPlaylistState

    data class AlreadyPresent(
        val playlistName: String,
        val feedbackWasShown: Boolean
    ) : AddTrackToPlaylistState

    data class ShowPlaylists(val playlists: List<Playlist>) : AddTrackToPlaylistState

}