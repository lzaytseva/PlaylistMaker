package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import com.practicum.playlistmaker.sharing.data.navigation.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository

object Creator {
    private fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigator(context)
    }

    fun provideSharingRepository(context: Context): SharingRepository {
        return SharingRepositoryImpl(provideExternalNavigator(context), context)
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

}