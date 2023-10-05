package com.practicum.playlistmaker.settings.domain.repository

import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings

    fun updateThemeSettings(settings: ThemeSettings)
}