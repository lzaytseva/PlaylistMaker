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
import com.practicum.playlistmaker.utils.ToastState

class SettingsViewModel(
    private val application: Application
) : AndroidViewModel(application) {


    private val sharingInteractor = Creator.provideSharingInteractor(getApplication())
    private val _toastState = MutableLiveData<ToastState>()
    val noApplicationsFound: LiveData<ToastState>
        get() = _toastState


    fun shareApp() {
        try {
            sharingInteractor.shareApp()
        } catch (e: Exception) {
            setStateShow()
        }
    }

    fun contactSupport() {
        try {
            sharingInteractor.contactSupport()
        } catch (e: Exception) {
            setStateShow()
        }
    }

    fun openTerms() {
        try {
            sharingInteractor.openTerms()
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