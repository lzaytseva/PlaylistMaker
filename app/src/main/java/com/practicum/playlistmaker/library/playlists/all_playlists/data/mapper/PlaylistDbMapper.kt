package com.practicum.playlistmaker.library.playlists.all_playlists.data.mapper

import android.net.Uri
import com.practicum.playlistmaker.library.core.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist

class PlaylistDbMapper {

    fun mapDomainToEntity(playlist: Playlist): PlaylistEntity {
        return with(playlist) {
            PlaylistEntity(
                id = id,
                name = name,
                description = description,
                coverUri = coverUri.toString(),
                numberOfTracks = numberOfTracks,
                tracksId = tracksId.joinToString(" ")
            )
        }
    }

    fun mapEntityToDomain(playlistEntity: PlaylistEntity): Playlist {
        return with(playlistEntity) {
            Playlist(
                id = id,
                name = name,
                description = description,
                coverUri = Uri.parse(coverUri),
                numberOfTracks = numberOfTracks,
                tracksId = tracksId.mapStringToListInt()
            )
        }
    }

    private fun String.mapStringToListInt(): List<Int> {
        return if (isEmpty()) {
            emptyList()
        } else {
            split(" ").map { it.toInt() }
        }
    }

    fun mapEntityListToDomain(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map {
            mapEntityToDomain(it)
        }
    }
}