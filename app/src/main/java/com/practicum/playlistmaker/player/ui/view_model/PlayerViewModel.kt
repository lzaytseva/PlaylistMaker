package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.FavTracksInteractor
import com.practicum.playlistmaker.player.domain.api.TrackPlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: TrackPlayerInteractor,
    private val favTracksInteractor: FavTracksInteractor
) : ViewModel() {

    val playerState = playerInteractor.getState()

    private val _timeProgress = MutableLiveData(INITIAL_TIME)
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    private var timerJob: Job? = null

    init {
        playerInteractor.preparePlayer(track.previewUrl)
    }

    val timeProgress: LiveData<String>
        get() = _timeProgress

    private fun play() {
        playerInteractor.play()
    }

    fun pause() {
        playerInteractor.pause()
    }

    private fun getFormattedCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }

    fun playbackControl() {
        when (playerState.value) {
            PlayerState.PLAYING -> {
                pause()
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                play()
            }

            else -> {}
        }
        updateTimer()
    }

    private fun updateTimer() {
        val time = getFormattedCurrentPlayerPosition()

        when (playerState.value) {
            PlayerState.PLAYING -> {
                _timeProgress.postValue(time)
                timerJob = viewModelScope.launch(Dispatchers.Default) {
                    delay(UPDATE_TIMER_DELAY_IN_MILLIS)
                    updateTimer()
                }
            }

            PlayerState.PAUSED -> {
                _timeProgress.postValue(time)
                timerJob?.cancel()
            }

            else -> {
                timerJob?.cancel()
                _timeProgress.postValue(INITIAL_TIME)
            }
        }
    }

    private fun releasePlayer() {
        playerInteractor.release()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (track.isFavorite) {
                favTracksInteractor.deleteTrack(track)
                changeFavState(false)
            } else {
                favTracksInteractor.addTrack(track)
                changeFavState(true)
            }
        }
    }

    private fun changeFavState(isFavorite: Boolean) {
        track.isFavorite = isFavorite
        _isFavorite.postValue(isFavorite)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    companion object {
        private const val UPDATE_TIMER_DELAY_IN_MILLIS = 300L
        private const val INITIAL_TIME = "00:00"
    }
}