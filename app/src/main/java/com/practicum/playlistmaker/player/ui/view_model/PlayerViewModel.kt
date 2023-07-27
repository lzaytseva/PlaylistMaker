package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class PlayerViewModel(private val trackUrl: String) : ViewModel() {

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private val looper = Looper.getMainLooper()
    private val handler = Handler(looper)

    private val _timeProgress = MutableLiveData<String>()
    val timeProgress: LiveData<String>
        get() = _timeProgress

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    private val _noPreview = MutableLiveData<Boolean>()
    val noPreview: LiveData<Boolean>
        get() = _noPreview


    init {
        preparePlayer()
    }

    private fun play() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        _isPlaying.value = true
        startTimer()
    }

    fun pause() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        _isPlaying.value = false
        stopTimer()
    }

    private fun startTimer() {
        handler.post(createUpdateTimerTask())
    }

    private fun stopTimer() {
        handler.removeCallbacks(createUpdateTimerTask())
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    _timeProgress.value = getFormattedCurrentPlayerPosition()
                    handler.postDelayed(this, UPDATE_TIMER_DELAY_IN_MILLIS)
                }
            }
        }
    }

    private fun getFormattedCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    private fun preparePlayer() {
        if (trackUrl.isNotEmpty()) {
            mediaPlayer.setDataSource(trackUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                _isPlaying.value = false
                stopTimer()
                _timeProgress.value = INITIAL_TIME
            }
        } else {
            _noPreview.value = true
        }
    }

    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pause()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                play()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATE_TIMER_DELAY_IN_MILLIS = 500L
        private const val INITIAL_TIME = "00:00"
    }
}