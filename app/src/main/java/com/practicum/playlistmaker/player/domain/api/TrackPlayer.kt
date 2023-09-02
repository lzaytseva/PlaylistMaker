package com.practicum.playlistmaker.player.domain.api

import androidx.lifecycle.LiveData
import com.practicum.playlistmaker.player.domain.model.PlayerState

interface TrackPlayer {
    fun preparePlayer(trackUrl: String)
    fun getState(): LiveData<PlayerState>
    fun play()
    fun pause()
    fun getCurrentPosition(): Int

    fun release()

}