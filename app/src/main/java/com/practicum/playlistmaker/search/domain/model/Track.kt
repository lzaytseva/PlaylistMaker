package com.practicum.playlistmaker.search.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val duration: String,
    val durationMillis: Long = 0,
    val artworkUrl100: String,
    val collectionName: String,
    val year: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean = false
) : Parcelable {

    val artworkUrl512
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    val artworkUrl60
        get() = artworkUrl100.replaceAfterLast('/', "60x60bb.jpg")
}


