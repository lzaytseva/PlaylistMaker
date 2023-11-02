package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.model.Track

class HistoryInteractorImpl(
    private val repository: HistoryRepository
) : HistoryInteractor {

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override suspend fun getAllTracks(): List<Track> {
        return repository.getAllTracks()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}