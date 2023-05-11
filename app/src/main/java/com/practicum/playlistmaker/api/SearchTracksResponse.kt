package com.practicum.playlistmaker.api

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.track.Track

class SearchTracksResponse(@SerializedName("results") val tracks: List<Track>)