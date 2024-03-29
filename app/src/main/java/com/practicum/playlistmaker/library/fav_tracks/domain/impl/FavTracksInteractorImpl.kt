package com.practicum.playlistmaker.library.fav_tracks.domain.impl

import com.practicum.playlistmaker.library.fav_tracks.domain.api.FavTracksInteractor
import com.practicum.playlistmaker.library.fav_tracks.domain.api.FavTracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavTracksInteractorImpl(
    val repository: FavTracksRepository
) : FavTracksInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override fun getAllTracks(): Flow<List<Track>> = flow {
        repository.getAllTracks().collect {
            emit(it.reversed())
        }
    }
}