package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.model.Track

interface HistoryInteractor {
    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}