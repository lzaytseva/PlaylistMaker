package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.storage.shared_prefs.SharedPrefsHistoryStorage
import com.practicum.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.sharing.data.navigation.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingRepository
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    private fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigator(context)
    }
    private fun provideSharingRepository(context: Context): SharingRepository {
        return SharingRepositoryImpl(provideExternalNavigator(context), context)
    }
    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(provideSharingRepository(context))
    }
}