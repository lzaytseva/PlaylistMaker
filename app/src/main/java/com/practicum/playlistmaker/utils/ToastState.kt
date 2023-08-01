package com.practicum.playlistmaker.utils

sealed interface ToastState {
    object None : ToastState
    data class Show(val additionalMessage: String) : ToastState
}