package com.practicum.playlistmaker.library.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getDrawable
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.library.domain.model.CreatePlaylistState
import com.practicum.playlistmaker.library.ui.view_model.CreatePlaylistViewModel
import com.practicum.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : BindingFragment<FragmentCreatePlaylistBinding>() {

    private val viewModel: CreatePlaylistViewModel by viewModel()
    private var coverUri: Uri? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia = registerPickMediaRequest()

        binding.ivPlaylistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCreate.setOnClickListener {
            viewModel.createPlaylist(
                name = binding.etPlaylistTitle.text.toString(),
                description = binding.etPlaylistDesc.text.toString(),
                uri = coverUri
            )
        }

        binding.etPlaylistTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnCreate.isEnabled = !s.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.arrowBack.setOnClickListener {
            viewModel.shouldCloseScreen(
                name = binding.etPlaylistTitle.text.toString(),
                description = binding.etPlaylistDesc.text.toString(),
                uri = coverUri
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.shouldCloseScreen(
                name = binding.etPlaylistTitle.text.toString(),
                description = binding.etPlaylistDesc.text.toString(),
                uri = coverUri
            )
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CreatePlaylistState.Saved -> {
                    showToast(state.name)
                    closeScreen()
                }

                is CreatePlaylistState.Editing -> {
                    if (state.isStarted) {
                        showDialog()
                    } else {
                        closeScreen()
                    }
                }
            }
        }
    }

    private fun showToast(name: String) {
        Toast.makeText(
            requireContext(),
            getString(R.string.playlist_created, name), Toast.LENGTH_SHORT
        ).show()
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finish_creating_dialog_title))
            .setMessage(getString(R.string.finish_creating_dialog_message))
            .setPositiveButton(getString(R.string.finish_creating_dialog_btn_positive))
            { _, _ -> closeScreen() }
            .setNegativeButton(getString(R.string.finish_creating_dialog_btn_negative))
            { _, _ -> }
            .show()
    }

    private fun closeScreen() {
        findNavController().navigateUp()
    }

    private fun registerPickMediaRequest() =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            //обрабатываем событие выбора пользователем фотографии
            if (uri != null) {
                coverUri = uri
                setImage(coverUri)
            }
        }

    private fun setImage(uri: Uri?) {
        Glide.with(this)
            .load(uri)
            .placeholder(getDrawable(requireContext(), R.drawable.ic_add_photo))
            .transform(
                CenterCrop(),
                RoundedCorners(
                    resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_player)
                ),
            )
            .into(binding.ivPlaylistCover)
    }
}