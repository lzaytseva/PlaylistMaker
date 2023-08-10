package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.impl.TrackPlayerInteractorImpl
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
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
    viewModel {
        (trackUrl: String) -> PlayerViewModel(trackUrl)
    }
}