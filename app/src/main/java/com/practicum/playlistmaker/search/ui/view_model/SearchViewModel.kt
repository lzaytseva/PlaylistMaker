package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.model.SearchActivityState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.ErrorType

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val searchInteractor = Creator.provideSearchInteractor(application)
    private val historyInteractor = Creator.provideHistoryInteractor(application)

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    private var isClickAllowed = true

    private val _state = MutableLiveData<SearchActivityState>()
    val state: LiveData<SearchActivityState>
        get() = _state

    fun searchDebounce(changedText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        if (changedText.isBlank()) {
            _state.value = SearchActivityState.SearchHistory(getHistory())
        } else {
            this.latestSearchText = changedText

            val searchRunnable = Runnable { searchRequest(changedText) }

            val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY_IN_MILLIS
            handler.postAtTime(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                postTime,
            )
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchActivityState.Loading)

            searchInteractor.searchTracks(newSearchText, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorType: ErrorType?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorType != null -> {
                            renderState(
                                SearchActivityState.Error(
                                    errorMessage = getApplication<Application>().getString(R.string.check_connection),
                                )
                            )
                        }

                        tracks.isEmpty() -> {
                            renderState(
                                SearchActivityState.Empty(
                                    message = getApplication<Application>().getString(R.string.nothing_found),
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
            })
        }
    }

    private fun renderState(state: SearchActivityState) {
        _state.postValue(state)
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(
                { isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY_IN_MILLIS
            )
        }
        return current
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
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val CLICK_DEBOUNCE_DELAY_IN_MILLIS = 1000L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }
}