package com.practicum.playlistmaker.creator

import android.content.SharedPreferences
import com.practicum.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.storage.shared_prefs.SharedPrefsHistoryStorage
import com.practicum.playlistmaker.search.domain.impl.HistoryInteractorImpl

class Creator(val sharedPreferences: SharedPreferences) {
    private fun getStorage() = SharedPrefsHistoryStorage(sharedPreferences)
    private fun getRepository() = HistoryRepositoryImpl(getStorage())
    fun getHistoryInteractor() = HistoryInteractorImpl(getRepository())
}