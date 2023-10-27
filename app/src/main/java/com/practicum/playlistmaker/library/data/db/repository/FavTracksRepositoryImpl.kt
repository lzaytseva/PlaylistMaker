package com.practicum.playlistmaker.library.data.db.repository

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.mapper.TrackDbMapper
import com.practicum.playlistmaker.library.domain.api.FavTracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavTracksRepositoryImpl(
    private val db: AppDatabase,
    private val mapper: TrackDbMapper
) : FavTracksRepository {

    override suspend fun addTrack(track: Track) {
        db.favTracksDao().addTrack(mapper.mapDomainToTrackEntity(track))
    }

    override suspend fun deleteTrack(track: Track) {
        db.favTracksDao().deleteTrack(mapper.mapDomainToTrackEntity(track))
    }

    override fun getAllTracks(): Flow<List<Track>> = flow {
        val tracks = db.favTracksDao().getAllTracks()
        emit(mapper.mapTrackEntityListToDomain(tracks))
    }
}