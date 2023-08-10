package com.practicum.playlistmaker.player.domain.impl

import androidx.lifecycle.LiveData
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.api.TrackPlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class TrackPlayerInteractorImpl(
    trackUrl: String
) : TrackPlayerInteractor, KoinComponent {

    private val trackPlayer: TrackPlayer by inject {
        parametersOf(trackUrl)
    }
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