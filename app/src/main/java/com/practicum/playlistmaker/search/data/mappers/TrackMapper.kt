package com.practicum.playlistmaker.search.data.mappers

import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.*

class TrackMapper(private val gson: Gson) {

    fun mapDtoToEntity(dto: TrackDto, isFavourite: Boolean) = Track(
        trackId = dto.trackId,
        trackName = dto.trackName,
        artistName = dto.artistName,
        durationMillis = dto.trackTimeMillis ?: 0,
        duration = dto.trackTimeMillis?.let { getDuration(it) }.orEmpty(),
        artworkUrl100 = dto.artworkUrl100.orEmpty(),
        collectionName = dto.collectionName.orEmpty(),
        year = dto.releaseDate?.let { getYear(it) }.orEmpty(),
        primaryGenreName = dto.primaryGenreName.orEmpty(),
        country = dto.country.orEmpty(),
        previewUrl = dto.previewUrl.orEmpty(),
        isFavorite = isFavourite
    )

    private fun getDuration(trackTimeMillis: Long): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    private fun getYear(releaseDate: String) = releaseDate.substringBefore("-")

    fun createTracksListFromJson(json: String): Array<Track> {
        return gson.fromJson(json, Array<Track>::class.java)
    }

    fun createJsonFromTracksList(tracks: Array<Track>): String {
        return gson.toJson(tracks)
    }
}