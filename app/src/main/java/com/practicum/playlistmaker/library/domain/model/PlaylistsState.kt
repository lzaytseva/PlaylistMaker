package com.practicum.playlistmaker.library.domain.model

sealed interface PlaylistsState {

    object Empty : PlaylistsState

    data class Content(val playlists: List<Playlist>) : PlaylistsState
}