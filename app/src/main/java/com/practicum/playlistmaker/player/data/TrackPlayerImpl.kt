package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrackPlayerImpl(private val mediaPlayer: MediaPlayer) : TrackPlayer {

    private val _playerState = MutableStateFlow(PlayerState.DEFAULT)
    override val playerState = _playerState.asStateFlow()

    override fun preparePlayer(trackUrl: String) {
        if (trackUrl.isNotEmpty()) {
            with(mediaPlayer) {
                try {
                    setDataSource(trackUrl)
                    prepareAsync()
                } catch (e: Exception) {
                    _playerState.value = PlayerState.ERROR
                }

                setOnPreparedListener {
                    _playerState.value = PlayerState.PREPARED
                }
                setOnCompletionListener {
                    _playerState.value = PlayerState.PREPARED
                }
            }
        }
    }

    override fun play() {
        mediaPlayer.start()
        _playerState.value = PlayerState.PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        _playerState.value = PlayerState.PAUSED
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun release() {
        mediaPlayer.release()
    }
}