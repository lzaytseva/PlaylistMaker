package com.practicum.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.TrackPlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    trackUrl: String,
    private val playerInteractor: TrackPlayerInteractor
) : ViewModel() {

    val playerState = playerInteractor.getState()

    private val looper = Looper.getMainLooper()
    private val handler = Handler(looper)
    private val timerRunnable = Runnable { updateTimer() }

    private val _timeProgress = MutableLiveData(INITIAL_TIME)

    init {
        playerInteractor.preparePlayer(trackUrl)
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
        when (playerState.value) {
            PlayerState.PLAYING -> {
                _timeProgress.value = getFormattedCurrentPlayerPosition()
                handler.postDelayed(timerRunnable, UPDATE_TIMER_DELAY_IN_MILLIS)
            }

            PlayerState.PAUSED -> {
                handler.removeCallbacks(timerRunnable)
            }

            else -> {
                handler.removeCallbacks(timerRunnable)
                _timeProgress.value = INITIAL_TIME
            }
        }
    }

    private fun releasePlayer() {
        playerInteractor.release()
        updateTimer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    companion object {
        private const val UPDATE_TIMER_DELAY_IN_MILLIS = 500L
        private const val INITIAL_TIME = "00:00"
    }
}