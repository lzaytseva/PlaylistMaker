package com.practicum.playlistmaker.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {
    private val BASE_URL = "https://itunes.apple.com/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val itunesService: ItunesApi = retrofit.create(ItunesApi::class.java)
}