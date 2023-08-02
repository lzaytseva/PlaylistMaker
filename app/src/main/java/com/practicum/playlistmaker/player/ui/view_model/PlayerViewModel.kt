package com.practicum.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.model.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val trackUrl: String) : ViewModel() {

    private val playerInteractor = Creator.provideTrackPlayerInteractor(trackUrl)
    val playerState = playerInteractor.getState()

    private val looper = Looper.getMainLooper()
    private val handler = Handler(looper)
    private val timerRunnable = Runnable { updateTimer() }

    private val _timeProgress = MutableLiveData<String>(INITIAL_TIME)
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
        fun getViewModelFactory(trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }
}