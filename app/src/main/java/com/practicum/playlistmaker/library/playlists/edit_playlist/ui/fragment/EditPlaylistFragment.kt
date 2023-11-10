package com.practicum.playlistmaker.library.playlists.edit_playlist.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.playlists.edit_playlist.domain.EditPlaylistScreenState
import com.practicum.playlistmaker.library.playlists.edit_playlist.ui.view_model.EditPlaylistViewModel
import com.practicum.playlistmaker.library.playlists.new_playlist.ui.fragment.CreatePlaylistFragment
import com.practicum.playlistmaker.util.FeedbackUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : CreatePlaylistFragment() {

    private val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }
    private var playlistId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().let {
            playlistId = it.getInt(ARGS_PLAYLIST_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        changeLabels()
        setKeyboardMode()
        setCoverClickListener()
        setArrowBackClickListener()
        addTitleTextWatcher()
        setBtnSaveClickListener()
        setInputFieldsColorsBehavior()

        observeViewModel()
    }

    override fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    private fun renderState(state: EditPlaylistScreenState) {
        when (state) {
            is EditPlaylistScreenState.CurrentData -> fillData(state)
            is EditPlaylistScreenState.Saved -> showSaved(state.name)
        }
    }

    private fun fillData(data: EditPlaylistScreenState.CurrentData) {
        binding.etPlaylistTitle.setText(data.playlistName)
        binding.etPlaylistDesc.setText(data.playlistDescription)
        setImage(data.coverUri)
        coverUri = data.coverUri
    }

    private fun showSaved(name: String) {
        FeedbackUtils.showSnackbar(
            requireView(),
            getString(
                R.string.playlist_updated,
                name
            )
        )
        findNavController().navigateUp()
    }

    private fun changeLabels() {
        binding.btnCreate.text = getString(R.string.save)
        binding.tvHeader.text = getString(R.string.edit)
    }

    private fun setBtnSaveClickListener() {
        binding.btnCreate.setOnClickListener {
            viewModel.saveChanges(
                uri = coverUri,
                name = binding.etPlaylistTitle.text.toString(),
                description = binding.etPlaylistDesc.text.toString()
            )
        }
    }

    override fun setArrowBackClickListener() {
        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Int): Bundle {
            return bundleOf(
                ARGS_PLAYLIST_ID to playlistId
            )
        }
    }
}