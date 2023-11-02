package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow


class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    override suspend fun searchTracks(expression: String): Flow<Resource<List<Track>>> {
        return repository.searchTracks(expression)
    }
}