package com.practicum.playlistmaker.library.playlists.new_playlist.domain.model

sealed interface CreatePlaylistState {

    data class Saved(val name: String): CreatePlaylistState

    data class Editing(val isStarted: Boolean): CreatePlaylistState
}