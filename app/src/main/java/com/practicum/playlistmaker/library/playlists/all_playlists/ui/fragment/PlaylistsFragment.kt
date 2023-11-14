package com.practicum.playlistmaker.library.playlists.all_playlists.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.PlaylistsState
import com.practicum.playlistmaker.library.playlists.all_playlists.ui.adapters.PlaylistAdapter
import com.practicum.playlistmaker.library.playlists.all_playlists.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.library.playlists.playlist_details.ui.fragment.PlaylistDetailsFragment
import com.practicum.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private val adapter = PlaylistAdapter {
        findNavController().navigate(
            R.id.action_libraryFragment_to_playlistDetailsFragment,
            PlaylistDetailsFragment.createArgs(playlistId = it.id)
        )
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPlaceholderErrorMessage()
        initRv()
        setBtnCreateClickListener()

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    private fun renderState(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Content -> showContent(state.playlists)
        }
    }

    private fun showEmpty() {
        adapter.submitList(emptyList())
        binding.rvPlaylists.visibility = View.GONE
        binding.placeholderErrorLayout.root.visibility = View.VISIBLE
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.placeholderErrorLayout.root.visibility = View.GONE
        binding.rvPlaylists.visibility = View.VISIBLE
        adapter.submitList(playlists)
    }

    private fun setPlaceholderErrorMessage() {
        binding.placeholderErrorLayout.placeholderMessage.text =
            getString(R.string.error_no_playlists)
    }

    private fun initRv() {
        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPlaylists.adapter = adapter
    }

    private fun setBtnCreateClickListener() {
        binding.btnCreatePlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_createPlaylistFragment)
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}