package com.practicum.playlistmaker.library.data.db.mapper

import android.net.Uri
import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.domain.model.Playlist

class PlaylistDbMapper {

    fun mapDomainToEntity(playlist: Playlist): PlaylistEntity {
        return with(playlist) {
            PlaylistEntity(
                id = id,
                name = name,
                description = description,
                coverUri = coverUri.toString(),
                numberOfTracks = 0,
                tracksId = ""
            )
        }
    }

    private fun mapEntityToDomain(playlistEntity: PlaylistEntity): Playlist {
        return with(playlistEntity) {
            Playlist(
                id = id,
                name = name,
                description = description,
                coverUri = Uri.parse(coverUri)
            )
        }
    }

    fun mapEntityListToDomain(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map {
            mapEntityToDomain(it)
        }
    }
}