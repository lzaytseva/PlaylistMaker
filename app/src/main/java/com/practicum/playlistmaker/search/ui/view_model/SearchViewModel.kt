package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.model.SearchActivityState
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

    private val _state = MutableLiveData<SearchActivityState>()
    val state: LiveData<SearchActivityState>
        get() = _state


    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            tracksSearchDebounce(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isBlank()) {
            _state.value = SearchActivityState.SearchHistory(getHistory())
        } else {
            renderState(SearchActivityState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(newSearchText)
                    .collect {
                        processResult(
                            foundTracks = it.first,
                            errorType = it.second
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
                    SearchActivityState.Error(
                        errorMessage = application.getString(R.string.check_connection),
                    )
                )
            }

            tracks.isEmpty() -> {
                renderState(
                    SearchActivityState.Empty(
                        message = application.getString(R.string.nothing_found),
                    )
                )
            }

            else -> {
                renderState(
                    SearchActivityState.Content(
                        tracks = tracks,
                    )
                )
            }
        }

    }

    private fun renderState(state: SearchActivityState) {
        _state.postValue(state)
    }

    fun saveTrack(track: Track) {
        historyInteractor.saveTrack(track)
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        _state.value = SearchActivityState.SearchHistory(
            emptyList()
        )
    }

    fun showHistory() {
        _state.value = SearchActivityState.SearchHistory(
            getHistory()
        )
    }

    private fun getHistory() = historyInteractor.getAllTracks()

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_IN_MILLIS = 2000L
    }
}