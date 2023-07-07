package com.practicum.playlistmaker.data.network

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.presentation.model.Track

data class SearchTracksResponse(@SerializedName("results") val tracks: List<Track>)