package com.practicum.playlistmaker.library.playlists.playlist_details.data.repository

import com.practicum.playlistmaker.library.core.data.db.AppDatabase
import com.practicum.playlistmaker.library.fav_tracks.data.mapper.TrackDbMapper
import com.practicum.playlistmaker.library.playlists.all_playlists.data.mapper.PlaylistDbMapper
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.api.PlaylistDetailsRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistDetailsRepositoryImpl(
    private val db: AppDatabase,
    private val playlistMapper: PlaylistDbMapper,
    private val trackMapper: TrackDbMapper
) : PlaylistDetailsRepository {
    override suspend fun getPlaylist(playlistId: Int): Playlist {
        val entity = db.playlistsDao().getPlaylist(playlistId)
        return playlistMapper.mapEntityToDomain(entity)
    }

    override fun getTracks(tracksId: List<Int>): Flow<List<Track>> {
        return flow {
            val allTracks = db.tracksDao().getAllTracks()
            emit(
                allTracks
                    .filter {
                        tracksId.contains(it.trackId)
                    }
                    .map {
                        trackMapper.mapPlaylistTrackEntityToDomain(it)
                    }
            )
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        db.playlistsDao().updateTracksInPlaylist(
            playlistMapper.mapDomainToEntity(playlist)
        )
    }
}