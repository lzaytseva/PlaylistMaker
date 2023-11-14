package com.practicum.playlistmaker.library.playlists.edit_playlist.domain

import android.net.Uri

sealed interface EditPlaylistScreenState {
    data class Saved(val name: String) : EditPlaylistScreenState

    data class CurrentData(
        val playlistName: String,
        val playlistDescription: String,
        val coverUri: Uri
    ) : EditPlaylistScreenState
}