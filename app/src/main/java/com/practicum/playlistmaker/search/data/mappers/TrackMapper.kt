package com.practicum.playlistmaker.search.data.mappers

import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.*

class TrackMapper {
    fun mapDtoToEntity(dto: TrackDto) = Track(
        trackId = dto.trackId,
        trackName = dto.trackName,
        artistName = dto.artistName,
        duration = dto.trackTimeMillis?.let { getDuration(it) } ?: EMPTY_STRING,
        artworkUrl100 = dto.artworkUrl100 ?: EMPTY_STRING,
        collectionName = dto.collectionName ?: EMPTY_STRING,
        year = dto.releaseDate?.let { getYear(it) } ?: EMPTY_STRING,
        primaryGenreName = dto.primaryGenreName ?: EMPTY_STRING,
        country = dto.country ?: EMPTY_STRING,
        previewUrl = dto.previewUrl ?: EMPTY_STRING
    )

    private fun getDuration(trackTimeMillis: Long): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    private fun getYear(releaseDate: String) = releaseDate.substringBefore("-")

    fun createTracksListFromJson(json: String): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    fun createJsonFromTracksList(tracks: Array<Track>): String {
        return Gson().toJson(tracks)
    }

    companion object {
        private const val EMPTY_STRING = ""
    }
}