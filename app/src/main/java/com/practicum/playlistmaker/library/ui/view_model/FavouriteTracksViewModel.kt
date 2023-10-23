package com.practicum.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.FavTracksInteractor
import com.practicum.playlistmaker.library.domain.model.FavTracksState
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val favTracksInteractor: FavTracksInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavTracksState>()
    val state: LiveData<FavTracksState>
        get() = _state

    fun getFavTracks() {
        viewModelScope.launch {
            favTracksInteractor.getAllTracks().collect {
                if (it.isEmpty()) {
                    _state.postValue(FavTracksState.Empty)
                } else {
                    _state.postValue(FavTracksState.Content(it))
                }
            }
        }
    }
}
