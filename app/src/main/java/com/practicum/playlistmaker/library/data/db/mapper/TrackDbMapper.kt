package com.practicum.playlistmaker.library.data.db.mapper

import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.model.Track

class TrackDbMapper {
    private fun mapEntityToDomain(trackEntity: TrackEntity): Track {
        return with(trackEntity) {
            Track(
                trackId,
                trackName,
                artistName,
                duration,
                artworkUrl100,
                collectionName,
                year,
                primaryGenreName,
                country,
                previewUrl,
                isFavorite = true
            )
        }
    }

    fun mapDomainToEntity(track: Track): TrackEntity {
        return with(track) {
            TrackEntity(
                trackId,
                trackName,
                artistName,
                duration,
                artworkUrl100,
                collectionName,
                year,
                primaryGenreName,
                country,
                previewUrl
            )
        }
    }

    fun mapEntityListToDomain(tracks: List<TrackEntity>): List<Track> {
        return tracks.map {
            mapEntityToDomain(it)
        }
    }
}