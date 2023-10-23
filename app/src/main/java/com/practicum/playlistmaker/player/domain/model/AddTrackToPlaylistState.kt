package com.practicum.playlistmaker.player.domain.model

import com.practicum.playlistmaker.library.domain.model.Playlist

sealed interface AddTrackToPlaylistState {
    data class WasAdded(val playlistName: String) : AddTrackToPlaylistState

    data class AlreadyPresent(val playlistName: String) : AddTrackToPlaylistState

    data class ShowPlaylists(val playlists: List<Playlist>) : AddTrackToPlaylistState

}