package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.dto.SearchTracksResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.mappers.TrackMapper
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.ErrorType
import com.practicum.playlistmaker.util.Resource

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val mapper: TrackMapper
) : SearchRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(ErrorType.NO_INTERNET)
            }

            200 -> {
                Resource.Success((response as SearchTracksResponse).tracks.map {
                    mapper.mapDtoToEntity(it)
                })
            }

            else -> {
                Resource.Error(ErrorType.SERVER_ERROR)
            }
        }
    }
}