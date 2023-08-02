package com.practicum.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.settings.ui.ToastState

class SettingsViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val sharingRepository = Creator.provideSharingRepository(application)
    private val settingsRepository = Creator.provideSettingsRepository(application)

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

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }
}