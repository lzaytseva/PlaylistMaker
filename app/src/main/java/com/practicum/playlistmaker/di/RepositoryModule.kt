package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.data.db.repository.FavTracksRepositoryImpl
import com.practicum.playlistmaker.library.domain.api.FavTracksRepository
import com.practicum.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import com.practicum.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<HistoryRepository> {
        HistoryRepositoryImpl(storage = get(), database = get())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(networkClient = get(), mapper = get(), favTracksDao = get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(externalNavigator = get(), context = androidContext())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }

    single<FavTracksRepository> {
        FavTracksRepositoryImpl(favTracksDao = get(), trackDbMapper = get())
    }
}