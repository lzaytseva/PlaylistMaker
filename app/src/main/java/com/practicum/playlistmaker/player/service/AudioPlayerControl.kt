package com.practicum.playlistmaker.player.service

import com.practicum.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {

    val playerState: StateFlow<PlayerState>

    fun playbackControl()

    val playbackProgress: StateFlow<Int>

    fun showNotification()

    fun hideNotification()
}