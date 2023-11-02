package com.practicum.playlistmaker.library.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override suspend fun savePlaylist(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        repository.getAllPlaylists().collect {
            emit(it)
        }
    }

    override fun saveCoverToStorage(uri: Uri?): Uri {
        return repository.saveCoverToStorage(uri)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val tracksId = playlist.tracksId.toMutableList().apply {
            add(track.trackId)
        }
        repository.updatePlaylist(playlist.copy(tracksId = tracksId, numberOfTracks = tracksId.size))
        repository.addTrack(track)
    }
}