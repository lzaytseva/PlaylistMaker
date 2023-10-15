package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.library.data.db.dao.FavTracksDao
import com.practicum.playlistmaker.search.data.storage.HistoryStorage
import com.practicum.playlistmaker.search.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.model.Track

class HistoryRepositoryImpl(
    private val storage: HistoryStorage,
    private val favTracksDao: FavTracksDao
) : HistoryRepository {

    override fun saveTrack(track: Track) {
        storage.saveTrack(track)
    }

    override suspend fun getAllTracks(): List<Track> {
        val tracks = storage.getAllTracks()
        val favTracksIds = favTracksDao.getIds()
        return tracks.map {
            it.copy(
                isFavorite = favTracksIds.contains(it.trackId)
            )
        }
    }

    override fun clearHistory() {
        storage.clearHistory()
    }
}