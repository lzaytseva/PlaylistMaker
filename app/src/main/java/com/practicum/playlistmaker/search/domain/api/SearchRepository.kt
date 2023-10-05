package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow


interface SearchRepository {
    suspend fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}