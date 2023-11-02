package com.practicum.playlistmaker.library.fav_tracks.data.mapper

import com.practicum.playlistmaker.library.core.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.library.core.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.model.Track

class TrackDbMapper {

    private fun mapTrackEntityToDomain(trackEntity: TrackEntity): Track {
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

    fun mapDomainToTrackEntity(track: Track): TrackEntity {
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
                previewUrl,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    fun mapDomainToPlaylistTrack(track: Track): PlaylistTrackEntity {
        return with(track) {
            PlaylistTrackEntity(
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
            )
        }
    }

    fun mapPlaylistTrackEntityToDomain(trackEntity: PlaylistTrackEntity): Track {
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
            )
        }
    }

    fun mapTrackEntityListToDomain(tracks: List<TrackEntity>): List<Track> {
        return tracks.map {
            mapTrackEntityToDomain(it)
        }
    }
}