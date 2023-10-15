package com.practicum.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.BindingFragment
import com.practicum.playlistmaker.util.ToastState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        viewModel.noApplicationsFound.observe(viewLifecycleOwner) {
            if (it is ToastState.Show) {
                showToast(it.additionalMessage)
                viewModel.toastWasShown()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}