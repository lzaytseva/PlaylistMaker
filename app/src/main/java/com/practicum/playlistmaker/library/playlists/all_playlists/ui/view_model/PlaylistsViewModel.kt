package com.practicum.playlistmaker.library.playlists.all_playlists.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>()
    val state: LiveData<PlaylistsState>
        get() = _state

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getAllPlaylists().collect {
                if (it.isEmpty()) {
                    _state.postValue(PlaylistsState.Empty)
                } else {
                    _state.postValue(PlaylistsState.Content(it))
                }
            }
        }
    }
}