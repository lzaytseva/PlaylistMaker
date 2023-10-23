package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.model.SearchScreenState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.ErrorType
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val application: Application,
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private var latestSearchText: String? = null
    private val tracksSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY_IN_MILLIS, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    private val _state = MutableLiveData<SearchScreenState>()
    val state: LiveData<SearchScreenState>
        get() = _state


    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            tracksSearchDebounce(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isBlank()) {
            viewModelScope.launch {
                _state.value = SearchScreenState.SearchHistory(getHistory())
            }
        } else {
            renderState(SearchScreenState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(newSearchText)
                    .collect {
                        processResult(
                            foundTracks = it.data,
                            errorType = it.errorType
                        )
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorType: ErrorType?) {
        val tracks = mutableListOf<Track>()

        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorType != null -> {
                renderState(
                    SearchScreenState.Error(
                        errorMessage = application.getString(R.string.check_connection),
                    )
                )
            }

            tracks.isEmpty() -> {
                renderState(
                    SearchScreenState.Empty(
                        message = application.getString(R.string.nothing_found),
                    )
                )
            }

            else -> {
                renderState(
                    SearchScreenState.Content(
                        tracks = tracks,
                    )
                )
            }
        }
    }

    private fun renderState(state: SearchScreenState) {
        _state.postValue(state)
    }

    fun saveTrack(track: Track) {
        historyInteractor.saveTrack(track)
        showHistory()
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        _state.value = SearchScreenState.SearchHistory(
            emptyList()
        )
    }

    fun showHistory() {
        viewModelScope.launch {
            _state.value = SearchScreenState.SearchHistory(
                getHistory()
            )
        }
    }

    private suspend fun getHistory() = historyInteractor.getAllTracks()

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_IN_MILLIS = 2000L
    }
}