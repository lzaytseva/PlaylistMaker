package com.practicum.playlistmaker.player.domain.impl

import androidx.lifecycle.LiveData
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.api.TrackPlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState

class TrackPlayerInteractorImpl(
    private val trackPlayer: TrackPlayer
): TrackPlayerInteractor {
    override fun getState(): LiveData<PlayerState> {
        return trackPlayer.getState()
    }


    override fun play() {
        trackPlayer.play()
    }

    override fun pause() {
        trackPlayer.pause()
    }

    override fun getCurrentPosition(): Int {
        return trackPlayer.getCurrentPosition()
    }

    override fun release() {
        trackPlayer.release()
    }
}