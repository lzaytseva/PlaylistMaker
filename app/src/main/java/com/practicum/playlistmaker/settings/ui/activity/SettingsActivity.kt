package com.practicum.playlistmaker.settings.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.settings.ui.ToastState

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.bthShareApp.setOnClickListener { viewModel.shareApp() }

        binding.bthContactSupport.setOnClickListener { viewModel.contactSupport() }

        binding.btnUserAgreement.setOnClickListener { viewModel.openTerms() }

        binding.switchDarkMode.isChecked = viewModel.isDarkMode()

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateThemeSettings(isChecked)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.noApplicationsFound.observe(this) {
            if (it is ToastState.Show) {
                showToast(it.additionalMessage)
                viewModel.toastWasShown()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}