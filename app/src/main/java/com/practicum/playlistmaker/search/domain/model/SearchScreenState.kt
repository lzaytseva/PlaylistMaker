package com.practicum.playlistmaker.search.domain.model

sealed interface SearchScreenState {

    object Loading : SearchScreenState

    data class Content(
        val tracks: List<Track>
    ) : SearchScreenState

    data class Error(val errorMessage: String) : SearchScreenState

    data class Empty(val message: String) : SearchScreenState

    data class SearchHistory(
        val tracks: List<Track>
    ) : SearchScreenState
}