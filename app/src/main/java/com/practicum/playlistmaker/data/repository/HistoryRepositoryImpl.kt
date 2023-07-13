package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.mappers.TrackMapper
import com.practicum.playlistmaker.data.storage.HistoryStorage
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.model.Track

class HistoryRepositoryImpl(private val storage: HistoryStorage): HistoryRepository {
    private val mapper = TrackMapper()
    override fun saveTrack(track: Track) {
        storage.saveTrack(mapper.mapDomainToDbModel(track))
    }

    override fun getAllTracks(): List<Track> {
        return mapper.mapDbModelTracksListToTrackList(storage.getAllTracks())
    }

    override fun clearHistory() {
        storage.clearHistory()
    }
}