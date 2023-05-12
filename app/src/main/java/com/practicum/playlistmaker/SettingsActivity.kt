package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.bthShareApp.setOnClickListener { startShareAppActivity() }

        binding.bthContactSupport.setOnClickListener { startContactSupportActivity() }

        binding.btnUserAgreement.setOnClickListener { startOpenUserAgreementActivity() }
    }

    private fun startShareAppActivity() {
        val message = getString(R.string.android_course_url)

        Intent().apply {
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

        Intent().apply {
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

        Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
            startActivity(this)
        }
    }
}