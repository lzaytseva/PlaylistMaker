package com.practicum.playlistmaker.library.domain.api

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavTracksInteractor {
    suspend fun addTrack(track: Track)

    suspend fun deleteTrack(trackId: Int)

    fun getAllTracks(): Flow<List<Track>>
}