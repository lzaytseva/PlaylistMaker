package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.library.core.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.SearchTracksResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.mappers.TrackMapper
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.ErrorType
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val mapper: TrackMapper,
    private val db: AppDatabase
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            RetrofitNetworkClient.RC_NO_INTERNET -> {
                emit(Resource.Error(ErrorType.NO_INTERNET))
            }

            RetrofitNetworkClient.RC_SUCCESS -> {
                val favTracksIds = db.favTracksDao().getIds()
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