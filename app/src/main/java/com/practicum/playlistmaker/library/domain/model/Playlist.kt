package com.practicum.playlistmaker.library.domain.model

import android.net.Uri
import com.practicum.playlistmaker.search.domain.model.Track

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String,
    val coverUri: Uri,
    val tracks: List<Track> = emptyList()
)