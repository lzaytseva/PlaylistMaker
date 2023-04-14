package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ImageView>(R.id.arrow_back)
        btnBack.setOnClickListener {
            finish()
        }

        val btnShareApp = findViewById<FrameLayout>(R.id.bth_share_app)
        btnShareApp.setOnClickListener { startShareAppActivity() }

        val btnContactSupport = findViewById<FrameLayout>(R.id.bth_contact_support)
        btnContactSupport.setOnClickListener { startContactSupportActivity() }

        val btnUserAgreement = findViewById<FrameLayout>(R.id.btn_user_agreement)
        btnUserAgreement.setOnClickListener { startOpenUserAgreementActivity() }
    }

    private fun startShareAppActivity() {
        val message = getString(R.string.android_course_url)

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
            startActivity(this)
        }
    }

    private fun startContactSupportActivity() {
        val emailTo = getString(R.string.support_email)
        val emailSubject = getString(R.string.contact_support_email_subject)
        val emailText = getString(R.string.contact_support_email_text)

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailTo))
            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            putExtra(Intent.EXTRA_TEXT, emailText)
            startActivity(this)
        }
    }
    private fun startOpenUserAgreementActivity() {
        val url = getString(R.string.practicum_offer)

        val browserIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
            startActivity(this)
        }
    }
}