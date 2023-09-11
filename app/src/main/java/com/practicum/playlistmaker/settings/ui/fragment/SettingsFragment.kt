package com.practicum.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.ToastState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}