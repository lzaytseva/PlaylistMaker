package com.practicum.playlistmaker.library.fav_tracks.domain.model

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface FavTracksState {

    object Empty : FavTracksState

    data class Content(val favTracks: List<Track>) : FavTracksState
}