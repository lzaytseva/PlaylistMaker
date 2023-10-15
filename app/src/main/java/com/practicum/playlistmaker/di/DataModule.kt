package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.mapper.TrackDbMapper
import com.practicum.playlistmaker.player.data.TrackPlayerImpl
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.search.data.mappers.TrackMapper
import com.practicum.playlistmaker.search.data.network.ItunesApi
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.storage.HistoryStorage
import com.practicum.playlistmaker.search.data.storage.shared_prefs.SharedPrefsHistoryStorage
import com.practicum.playlistmaker.search.data.storage.shared_prefs.SharedPrefsHistoryStorage.Companion.PLAYLIST_MAKER_PREFERENCES
import com.practicum.playlistmaker.sharing.data.navigation.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<HistoryStorage> {
        SharedPrefsHistoryStorage(sharedPrefs = get(), mapper = get())
    }

    single {
        androidContext()
            .getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory {
        TrackMapper(gson = get())
    }

    factory {
        Gson()
    }

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl(RetrofitNetworkClient.ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            context = androidContext(), itunesService = get()
        )
    }

    single {
        ExternalNavigator(context = androidContext())
    }

    factory<TrackPlayer> {
        TrackPlayerImpl(mediaPlayer = get())
    }

    factory {
        MediaPlayer()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    factory {
        TrackDbMapper()
    }
}