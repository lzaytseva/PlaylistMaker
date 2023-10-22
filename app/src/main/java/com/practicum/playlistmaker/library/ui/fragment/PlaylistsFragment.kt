package com.practicum.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.domain.model.Playlist
import com.practicum.playlistmaker.library.domain.model.PlaylistsState
import com.practicum.playlistmaker.library.ui.adapters.PlaylistAdapter
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private val adapter = PlaylistAdapter()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.placeholderErrorLayout.placeholderMessage.text =
            getString(R.string.error_no_playlists)

        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPlaylists.adapter = adapter

        binding.btnCreatePlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_createPlaylistFragment)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
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

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}