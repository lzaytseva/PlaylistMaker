package com.practicum.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

data class SearchTracksResponse(@SerializedName("results") val tracks: List<TrackDto>)