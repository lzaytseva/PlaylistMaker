package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.fav_tracks.domain.api.FavTracksInteractor
import com.practicum.playlistmaker.library.fav_tracks.domain.impl.FavTracksInteractorImpl
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.impl.PlaylistsInteractorImpl
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.api.PlaylistDetailsInteractor
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.impl.PlaylistDetailsInteractorImpl
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
        TrackPlayerInteractorImpl(trackPlayer = get())
    }

    single<FavTracksInteractor> {
        FavTracksInteractorImpl(repository = get())
    }

    single<PlaylistsInteractor> {
        PlaylistsInteractorImpl(repository = get())
    }

    single<PlaylistDetailsInteractor> {
        PlaylistDetailsInteractorImpl(repository = get())
    }
}