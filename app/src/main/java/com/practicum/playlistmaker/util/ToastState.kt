package com.practicum.playlistmaker.util

sealed interface ToastState {
    object None : ToastState

    data class Show(val additionalMessage: String) : ToastState
}