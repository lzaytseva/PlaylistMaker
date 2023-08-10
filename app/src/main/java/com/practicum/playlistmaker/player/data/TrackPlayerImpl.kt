package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.model.PlayerState

class TrackPlayerImpl(private val mediaPlayer: MediaPlayer) : TrackPlayer {

    private val playerState = MutableLiveData<PlayerState>(PlayerState.DEFAULT)

    override fun preparePlayer(trackUrl: String) {
        if (trackUrl.isNotEmpty()) {
            with(mediaPlayer) {
                try {
                    setDataSource(trackUrl)
                    prepareAsync()
                } catch (e: Exception) {
                    playerState.value = PlayerState.ERROR
                }

                setOnPreparedListener {
                    playerState.value = PlayerState.PREPARED
                }
                setOnCompletionListener {
                    playerState.value = PlayerState.PREPARED
                }
            }
        }
    }

    override fun getState(): LiveData<PlayerState> {
        return playerState
    }

    override fun play() {
        mediaPlayer.start()
        playerState.value = PlayerState.PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState.value = PlayerState.PAUSED
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun release() {
        mediaPlayer.release()
    }
}