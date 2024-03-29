package com.practicum.playlistmaker.library.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_tracks_table")
data class TrackEntity(
    @PrimaryKey
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val duration: String,
    val artworkUrl100: String,
    val collectionName: String,
    val year: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val timestamp: Long
)