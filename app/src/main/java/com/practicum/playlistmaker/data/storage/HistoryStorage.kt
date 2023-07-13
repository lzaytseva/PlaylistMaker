package com.practicum.playlistmaker.data.storage

import com.practicum.playlistmaker.data.storage.model.TrackDbModel

interface HistoryStorage {
    fun saveTrack(track: TrackDbModel)

    fun getAllTracks(): List<TrackDbModel>

    fun clearHistory()
}