package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.api.TrackPlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.TrackPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<SearchInteractor> {
        SearchInteractorImpl(repository = get())
    }

    single<HistoryInteractor> {
        HistoryInteractorImpl(repository = get())
    }

    factory<TrackPlayerInteractor> {
        (trackUrl: String) -> TrackPlayerInteractorImpl(trackUrl = trackUrl)
    }
}