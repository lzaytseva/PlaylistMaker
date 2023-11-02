package com.practicum.playlistmaker.library.playlists.playlist_details.domain.model

import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

data class PlaylistDetails(
    val playlist: Playlist,
    val tracks: List<Track>?
) {
    val totalDuration: String
        get() {
            val totalMillis = tracks?.sumOfDuration() ?: 0
            return SimpleDateFormat("mm", Locale.getDefault()).format(totalMillis)
        }
}

private fun List<Track>.sumOfDuration(): Long {
    var sum = 0L
    forEach {
        sum += it.duration.toLong()
    }
    return sum
}


