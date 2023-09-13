package com.practicum.playlistmaker.sharing.data.repository

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.navigation.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository

class SharingRepositoryImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context
) : SharingRepository {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun contactSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.android_course_url)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            emailTo = context.getString(R.string.support_email),
            emailSubject = context.getString(R.string.contact_support_email_subject),
            emailText = context.getString(R.string.contact_support_email_text)
        )
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.practicum_offer)
    }
}