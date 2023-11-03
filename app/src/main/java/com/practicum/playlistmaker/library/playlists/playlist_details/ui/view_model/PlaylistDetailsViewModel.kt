package com.practicum.playlistmaker.library.playlists.playlist_details.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.api.PlaylistDetailsInteractor
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.model.PlaylistDetails
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.model.PlaylistDetailsScreenState
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistId: Int,
    private val playlistDetailsInteractor: PlaylistDetailsInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistDetailsScreenState>()
    val state: LiveData<PlaylistDetailsScreenState>
        get() = _state

    private lateinit var playlist: Playlist
    private lateinit var tracks: List<Track>

    init {
        getPlaylistDetails(playlistId)
    }

    private fun getPlaylistDetails(playlistId: Int) {
        viewModelScope.launch {
            playlist = getPlaylist(playlistId)
            tracks = getTracks(playlist.tracksId)
            _state.postValue(
                if (tracks.isEmpty()) {
                    PlaylistDetailsScreenState.EmptyPlaylist(
                        PlaylistDetails(
                            playlist = playlist,
                            tracks = null
                        )
                    )
                } else {
                    PlaylistDetailsScreenState.FullContent(
                        PlaylistDetails(
                            playlist = playlist,
                            tracks = tracks
                        )
                    )
                }
            )
        }
    }

    private suspend fun getPlaylist(playlistId: Int): Playlist {
        return playlistDetailsInteractor.getPlaylist(playlistId)
    }

    private suspend fun getTracks(tracksIds: List<Int>): List<Track> {
        return playlistDetailsInteractor.getTracks(tracksIds).first()
    }

    fun deleteTrack(track: Track) {
        val newTracksIds = playlist.tracksId.toMutableList()
        newTracksIds.remove(track.trackId)
        val updatedPlaylist = playlist.copy(tracksId = newTracksIds)
        viewModelScope.launch {
            playlistDetailsInteractor.updatePlaylist(updatedPlaylist)
            getPlaylistDetails(playlistId)
            playlistDetailsInteractor.deleteTrackFromTable(track)
        }
    }

    fun sharePlaylist() {
        TODO("Not yet implemented")
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistDetailsInteractor.deletePlaylist(playlist, tracks)
            _state.postValue(PlaylistDetailsScreenState.PlaylistDeleted)
        }

    }
}