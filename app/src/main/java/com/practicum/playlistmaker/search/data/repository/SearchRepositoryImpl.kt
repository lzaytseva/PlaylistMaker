package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.SearchTracksResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.mappers.TrackMapper
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.ErrorType
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val mapper: TrackMapper,
    private val database: AppDatabase
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(ErrorType.NO_INTERNET))
            }

            200 -> {
                val favTracksIds = database.favTracksDao().getIds()
                emit(Resource.Success((response as SearchTracksResponse).tracks.map {
                    mapper.mapDtoToEntity(
                        dto = it,
                        isFavourite = favTracksIds.contains(it.trackId)
                    )
                }))
            }

            else -> {
                emit(Resource.Error(ErrorType.SERVER_ERROR))
            }
        }
    }

}