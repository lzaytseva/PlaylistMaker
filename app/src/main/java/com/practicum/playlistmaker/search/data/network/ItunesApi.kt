package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.SearchTracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {

    @GET("search?entity=song")
    suspend fun search(@Query("term") text: String): SearchTracksResponse
}