package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface TrackPlayer {

    val playerState: StateFlow<PlayerState>

    fun preparePlayer(trackUrl: String)

    fun play()

    fun pause()

    fun getCurrentPosition(): Int

    fun release()
}