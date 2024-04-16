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
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.service.PlayerService
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val favTracksInteractor: FavTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var playerService: PlayerService? = null
    val playerState by lazy {
        playerService?.playerState?.asLiveData()
    }

    private val _timeProgress = MutableLiveData(INITIAL_TIME)
    val timeProgress: LiveData<String>
        get() = _timeProgress
    private var timerJob: Job? = null

    private val formatter: SimpleDateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    private val _addTrackState = MutableLiveData<AddTrackToPlaylistState>()
    val addTrackState: LiveData<AddTrackToPlaylistState>
        get() = _addTrackState


    fun setPlayerService(service: PlayerService) {
        playerService = service
    }

    private fun getFormattedCurrentPlayerPosition(): String {
        return formatter.format(playerService?.getCurrentPosition())
    }

    fun playbackControl() {
        playerService?.playbackControl()
        updateTimer()
    }

    fun hideServiceNotification() {
        playerService?.hideNotification()
    }

    fun showServiceNotification() {
        playerService?.showNotification()
    }

    private fun updateTimer() {
        val time = getFormattedCurrentPlayerPosition()

        when (playerState?.value) {
            PlayerState.PLAYING -> {
                _timeProgress.postValue(time)
                timerJob = viewModelScope.launch(Dispatchers.Default) {
                    delay(UPDATE_TIMER_DELAY_IN_MILLIS)
                    updateTimer()
                }
            }

            PlayerState.PAUSED -> {
                _timeProgress.postValue(time)
                timerJob?.cancel()
            }

            else -> {
                timerJob?.cancel()
                _timeProgress.postValue(INITIAL_TIME)
            }
        }
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

    companion object {
        private const val UPDATE_TIMER_DELAY_IN_MILLIS = 300L
        private const val INITIAL_TIME = "00:00"
    }
}