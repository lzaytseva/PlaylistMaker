package com.practicum.playlistmaker.search.data.storage

import com.practicum.playlistmaker.search.domain.model.Track

interface HistoryStorage {
    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}