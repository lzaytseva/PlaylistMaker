package com.practicum.playlistmaker.library.domain.model

sealed interface CreatePlaylistState {
    data class Saved(val name: String): CreatePlaylistState
    data class Editing(val isStarted: Boolean): CreatePlaylistState
}