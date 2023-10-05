package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.storage.HistoryStorage
import com.practicum.playlistmaker.search.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.model.Track

class HistoryRepositoryImpl(private val storage: HistoryStorage) : HistoryRepository {

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