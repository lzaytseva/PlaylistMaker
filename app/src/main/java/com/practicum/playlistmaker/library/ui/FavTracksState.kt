package com.practicum.playlistmaker.library.ui

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface FavTracksState {
    object Empty: FavTracksState
    data class Content(val favTracks: List<Track>): FavTracksState
}