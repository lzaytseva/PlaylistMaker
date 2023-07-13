package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.practicum.playlistmaker.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.storage.shared_prefs.SharedPrefsHistoryStorage
import com.practicum.playlistmaker.domain.impl.HistoryInteractorImpl

class Creator(val sharedPreferences: SharedPreferences) {
    private fun getStorage() = SharedPrefsHistoryStorage(sharedPreferences)
    private fun getRepository() = HistoryRepositoryImpl(getStorage())
    fun getHistoryInteractor() = HistoryInteractorImpl(getRepository())
}