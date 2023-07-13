package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()

}