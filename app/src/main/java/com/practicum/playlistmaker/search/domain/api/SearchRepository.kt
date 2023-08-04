package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.Resource


interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}