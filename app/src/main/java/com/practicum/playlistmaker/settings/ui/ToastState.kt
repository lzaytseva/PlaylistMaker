package com.practicum.playlistmaker.settings.ui

sealed interface ToastState {
    object None : ToastState
    data class Show(val additionalMessage: String) : ToastState
}