package com.practicum.playlistmaker.data.mappers

import com.google.gson.Gson
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.storage.model.TrackDbModel
import com.practicum.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackMapper {
    fun mapDtoToEntity(dto: TrackDto) = Track(
        trackId = dto.trackId,
        trackName = dto.trackName,
        artistName = dto.artistName,
        duration = getDuration(dto.trackTimeMillis),
        artworkUrl100 = dto.artworkUrl100,
        collectionName = dto.collectionName,
        year = getYear(dto.releaseDate),
        primaryGenreName = dto.primaryGenreName,
        country = dto.country,
        previewUrl = dto.previewUrl
    )

    fun mapDomainToDbModel(track: Track) = TrackDbModel(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        duration = track.duration,
        artworkUrl100 = track.artworkUrl100,
        collectionName = track.collectionName,
        year = track.year,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        previewUrl = track.previewUrl
    )

    private fun mapDbModelToDomain(dbModel: TrackDbModel) = Track(
        trackId = dbModel.trackId,
        trackName = dbModel.trackName,
        artistName = dbModel.artistName,
        duration = dbModel.duration,
        artworkUrl100 = dbModel.artworkUrl100,
        collectionName = dbModel.collectionName,
        year = dbModel.year,
        primaryGenreName = dbModel.primaryGenreName,
        country = dbModel.country,
        previewUrl = dbModel.previewUrl
    )

    fun mapDbModelTracksListToTrackList(tracks: List<TrackDbModel>) =
        tracks.map { mapDbModelToDomain(it) }


    private fun getDuration(trackTimeMillis: Long): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    private fun getYear(releaseDate: String) = releaseDate.substringBefore("-")

    fun createTracksListFromJson(json: String): Array<TrackDbModel> {
        return Gson().fromJson(json, Array<TrackDbModel>::class.java)
    }

    fun createJsonFromTracksList(tracks: Array<TrackDbModel>): String {
        return Gson().toJson(tracks)
    }
}