package com.practicum.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository
import com.practicum.playlistmaker.util.ToastState

class SettingsViewModel(
    private val application: Application,
    private val sharingRepository: SharingRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _toastState = MutableLiveData<ToastState>()
    val noApplicationsFound: LiveData<ToastState>
        get() = _toastState


    fun shareApp() {
        try {
            sharingRepository.shareApp()
        } catch (e: Exception) {
            setStateShow()
        }
    }

    fun contactSupport() {
        try {
            sharingRepository.contactSupport()
        } catch (e: Exception) {
            setStateShow()
        }
    }

    fun openTerms() {
        try {
            sharingRepository.openTerms()
        } catch (e: Exception) {
            setStateShow()
        }
    }

    private fun setStateShow() {
        _toastState.value = ToastState.Show(getErrorMessage())
    }

    fun toastWasShown() {
        _toastState.value = ToastState.None
    }

    fun isDarkMode(): Boolean {
        return settingsRepository.getThemeSettings().darkMode
    }

    fun updateThemeSettings(isDarkModeChecked: Boolean) {
        settingsRepository.updateThemeSettings(
            ThemeSettings(isDarkModeChecked)
        )
    }

    private fun getErrorMessage(): String {
        return application.getString(R.string.no_applications_found)
    }
}