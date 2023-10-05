package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.ErrorType
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    suspend fun searchTracks(expression: String): Flow<Pair<List<Track>?, ErrorType?>>

}