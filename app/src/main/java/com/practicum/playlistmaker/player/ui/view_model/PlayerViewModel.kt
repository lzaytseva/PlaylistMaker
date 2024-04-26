package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.fav_tracks.domain.api.FavTracksInteractor
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.AddTrackToPlaylistState
import com.practicum.playlistmaker.player.service.AudioPlayerControl
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val favTracksInteractor: FavTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var playerControl: AudioPlayerControl? = null

    val playerState by lazy {
        playerControl?.playerState?.asLiveData()
    }
    val timeProgress by lazy {
        playerControl?.playbackProgress
            ?.map { formatter.format(it) }
            ?.asLiveData()
    }
    private val formatter: SimpleDateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }


    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    private val _addTrackState = MutableLiveData<AddTrackToPlaylistState>()
    val addTrackState: LiveData<AddTrackToPlaylistState>
        get() = _addTrackState


    fun setPlayerControl(audioPlayerControl: AudioPlayerControl) {
        playerControl = audioPlayerControl
    }

    fun removeControl() {
        playerControl = null
    }


    fun playbackControl() {
        playerControl?.playbackControl()
    }

    fun hideServiceNotification() {
        playerControl?.hideNotification()
    }

    fun showServiceNotification() {
        playerControl?.showNotification()
    }


    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (track.isFavorite) {
                favTracksInteractor.deleteTrack(track)
                changeFavState(false)
            } else {
                favTracksInteractor.addTrack(track)
                changeFavState(true)
            }
        }
    }

    private fun changeFavState(isFavorite: Boolean) {
        track.isFavorite = isFavorite
        _isFavorite.postValue(isFavorite)
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        if (playlist.tracksId.contains(track.trackId)) {
            _addTrackState.value = AddTrackToPlaylistState.AlreadyPresent(
                playlistName = playlist.name,
                feedbackWasShown = false
            )
        } else {
            viewModelScope.launch {
                playlistsInteractor.addTrackToPlaylist(track, playlist)
                _addTrackState.postValue(
                    AddTrackToPlaylistState.WasAdded(
                        playlistName = playlist.name,
                        feedbackWasShown = false
                    )
                )
            }

        }
    }

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getAllPlaylists().collect {
                _addTrackState.postValue(AddTrackToPlaylistState.ShowPlaylists(it))
            }
        }
    }

    fun setFeedbackWasShown(state: AddTrackToPlaylistState) {
        if (state is AddTrackToPlaylistState.WasAdded) {
            _addTrackState.value = state
        }
        if (state is AddTrackToPlaylistState.AlreadyPresent) {
            _addTrackState.value = state
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerControl = null
    }
}