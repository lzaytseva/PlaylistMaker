package com.practicum.playlistmaker.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlayerViewModelFactory(private val trackUrl: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(trackUrl) as T
    }
}