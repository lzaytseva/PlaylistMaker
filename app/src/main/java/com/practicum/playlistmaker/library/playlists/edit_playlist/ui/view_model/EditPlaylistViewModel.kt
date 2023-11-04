package com.practicum.playlistmaker.library.playlists.edit_playlist.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.library.playlists.edit_playlist.domain.EditPlaylistScreenState
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.api.PlaylistDetailsInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    //TODO: мб сделать один общий интерактор? надо посмотреть, что где используется
    private val playlistDetailsInteractor: PlaylistDetailsInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistId: Int
) : ViewModel() {

    private val _state = MutableLiveData<EditPlaylistScreenState>()
    val state: LiveData<EditPlaylistScreenState>
        get() = _state

    init {
        getPlaylistCurrentInfo()
    }

    private fun getPlaylistCurrentInfo() {
        viewModelScope.launch {
            val playlist = playlistDetailsInteractor.getPlaylist(playlistId)
            _state.postValue(
                EditPlaylistScreenState.CurrentData(
                    playlistName = playlist.name,
                    playlistDescription = playlist.description,
                    coverUri = playlist.coverUri
                )
            )
        }
    }

    fun saveChanges(uri: Uri?, name: String, description: String?) {
        val uriInternalStorage = playlistsInteractor.saveCoverToStorage(uri)

        viewModelScope.launch {
            playlistDetailsInteractor.updatePlaylist(
                Playlist(
                    id = playlistId,
                    name = name,
                    description = description.orEmpty(),
                    coverUri = uriInternalStorage,
                )
            )

            _state.postValue(EditPlaylistScreenState.Saved(name))
        }
    }
}