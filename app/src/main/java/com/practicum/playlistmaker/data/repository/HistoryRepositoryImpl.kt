package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.storage.HistoryStorage
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class HistoryRepositoryImpl(private val storage: HistoryStorage): HistoryRepository {
    override fun saveTrack(track: Track) {
        storage.saveTrack(track)
    }

    override fun getAllTracks(): List<Track> {
        return storage.getAllTracks()
    }

    override fun clearHistory() {
        storage.clearHistory()
    }
}