package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface HistoryInteractor {
    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}