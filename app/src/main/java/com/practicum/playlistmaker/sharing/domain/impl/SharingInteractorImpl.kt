package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingRepository

class SharingInteractorImpl(private val repository: SharingRepository) : SharingInteractor {
    override fun shareApp() {
        repository.shareApp()
    }

    override fun openTerms() {
        repository.openTerms()
    }

    override fun contactSupport() {
        repository.contactSupport()
    }

}