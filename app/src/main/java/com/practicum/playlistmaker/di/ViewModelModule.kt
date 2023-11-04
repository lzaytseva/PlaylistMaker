package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.fav_tracks.ui.view_model.FavouriteTracksViewModel
import com.practicum.playlistmaker.library.playlists.all_playlists.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.library.playlists.edit_playlist.ui.view_model.EditPlaylistViewModel
import com.practicum.playlistmaker.library.playlists.new_playlist.ui.view_model.CreatePlaylistViewModel
import com.practicum.playlistmaker.library.playlists.playlist_details.ui.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(
            application = androidApplication(),
            searchInteractor = get(),
            historyInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            application = androidApplication(),
            sharingRepository = get(),
            settingsRepository = get()
        )
    }

    viewModel { (track: Track) ->
        PlayerViewModel(
            track = track,
            playerInteractor = get(),
            favTracksInteractor = get(),
            playlistsInteractor = get()
        )
    }

    viewModel {
        FavouriteTracksViewModel(favTracksInteractor = get())
    }

    viewModel {
        PlaylistsViewModel(playlistsInteractor = get())
    }

    viewModel {
        CreatePlaylistViewModel(playlistsInteractor = get())
    }

    viewModel {(playlistId: Int) ->
        PlaylistDetailsViewModel(
            playlistId,
            playlistDetailsInteractor = get()
        )
    }

    viewModel {(playlistId: Int) ->
        EditPlaylistViewModel(
            playlistDetailsInteractor = get(),
            playlistsInteractor = get(),
            playlistId
        )
    }
}