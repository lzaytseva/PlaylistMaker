package com.practicum.playlistmaker.library.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.model.CreatePlaylistState
import com.practicum.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _state = MutableLiveData<CreatePlaylistState>()
    val state: LiveData<CreatePlaylistState>
        get() = _state

    fun createPlaylist(uri: Uri?, name: String, description: String?) {
        viewModelScope.launch {
            //TODO: Надо ли переключать на IO?
            val uriInternalStorage = playlistsInteractor.saveCoverToStorage(uri)

            playlistsInteractor.savePlaylist(
                Playlist(
                    name = name,
                    description = description.orEmpty(),
                    coverUri = uriInternalStorage,
                )
            )

            _state.postValue(CreatePlaylistState.Saved(name))
        }
    }

    fun shouldCloseScreen(uri: Uri?, name: String, description: String) {
        val isEditingStarted = uri != null || name.isNotEmpty() || description.isNotEmpty()
        _state.value = CreatePlaylistState.Editing(isEditingStarted)
    }
}
