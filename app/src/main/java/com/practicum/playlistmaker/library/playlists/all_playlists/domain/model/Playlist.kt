package com.practicum.playlistmaker.library.playlists.all_playlists.domain.model

import android.net.Uri

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String,
    val coverUri: Uri,
    val tracksId: List<Int> = emptyList(),
) {
    val numberOfTracks: Int
        get() = tracksId.size
}