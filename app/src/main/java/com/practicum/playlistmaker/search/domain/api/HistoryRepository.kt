package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface HistoryRepository {

    fun saveTrack(track: Track)

    suspend fun getAllTracks(): List<Track>

    fun clearHistory()

}