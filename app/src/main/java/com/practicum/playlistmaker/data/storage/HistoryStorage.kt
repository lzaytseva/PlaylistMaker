package com.practicum.playlistmaker.data.storage

import com.practicum.playlistmaker.domain.model.Track

interface HistoryStorage {
    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}