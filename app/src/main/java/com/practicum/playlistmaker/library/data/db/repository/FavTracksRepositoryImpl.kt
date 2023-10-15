package com.practicum.playlistmaker.library.data.db.repository

import com.practicum.playlistmaker.library.data.db.dao.FavTracksDao
import com.practicum.playlistmaker.library.data.db.mapper.TrackDbMapper
import com.practicum.playlistmaker.library.domain.api.FavTracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavTracksRepositoryImpl(
    private val favTracksDao: FavTracksDao,
    private val trackDbMapper: TrackDbMapper
) : FavTracksRepository {

    override suspend fun addTrack(track: Track) {
        favTracksDao.addTrack(trackDbMapper.mapDomainToEntity(track))
    }

    override suspend fun deleteTrack(track: Track) {
        favTracksDao.deleteTrack(trackDbMapper.mapDomainToEntity(track))
    }

    override fun getAllTracks(): Flow<List<Track>> = flow {
        val tracks = favTracksDao.getAllTracks()
        emit(trackDbMapper.mapEntityListToDomain(tracks))
    }
}