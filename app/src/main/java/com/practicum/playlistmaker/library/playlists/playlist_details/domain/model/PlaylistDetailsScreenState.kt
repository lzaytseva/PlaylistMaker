package com.practicum.playlistmaker.library.playlists.playlist_details.domain.model

sealed interface PlaylistDetailsScreenState {
    data class EmptyPlaylist(val playlistInfo: PlaylistDetails): PlaylistDetailsScreenState

    data class FullContent(val playlistInfo: PlaylistDetails): PlaylistDetailsScreenState

    object PlaylistDeleted: PlaylistDetailsScreenState

    data class NoApplicationFound(val feedbackWasShown: Boolean): PlaylistDetailsScreenState

    data class NothingToShare(val feedbackWasShown: Boolean): PlaylistDetailsScreenState
}