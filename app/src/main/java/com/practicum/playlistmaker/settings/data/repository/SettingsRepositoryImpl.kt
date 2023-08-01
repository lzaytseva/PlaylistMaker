package com.practicum.playlistmaker.settings.data.repository

import android.content.Context
import com.practicum.playlistmaker.app.App
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            darkMode = (context as App).darkTheme
        )
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        (context as App).switchTheme(settings.darkMode)
    }
}