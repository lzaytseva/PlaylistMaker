package com.practicum.playlistmaker.library.fav_tracks.domain.api

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavTracksRepository {

    suspend fun addTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    fun getAllTracks(): Flow<List<Track>>
}