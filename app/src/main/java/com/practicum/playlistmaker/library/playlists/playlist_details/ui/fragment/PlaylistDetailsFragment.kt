package com.practicum.playlistmaker.library.playlists.playlist_details.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.model.PlaylistDetails
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.model.PlaylistDetailsScreenState
import com.practicum.playlistmaker.library.playlists.playlist_details.ui.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.player.ui.fragment.PlayerFragment
import com.practicum.playlistmaker.search.ui.adapters.TrackAdapter
import com.practicum.playlistmaker.util.BindingFragment
import com.practicum.playlistmaker.util.setTextOrHide
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : BindingFragment<FragmentPlaylistDetailsBinding>() {

    private var playlistId: Int = 0
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(playlistId)
    }
    private val adapter = TrackAdapter {
        findNavController().navigate(
            R.id.action_playlistDetailsFragment_to_playerFragment,
            PlayerFragment.createArgs(track = it)
        )
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistDetailsBinding {
        return FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().let {
            playlistId = it.getInt(ARGS_PLAYLIST_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomSheetBehavior()
        initTracksRv()
        setArrowBackClickListener()

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    private fun renderState(state: PlaylistDetailsScreenState) {
        when (state) {
            is PlaylistDetailsScreenState.FullContent -> showFullContent(state.playlistInfo)
            is PlaylistDetailsScreenState.EmptyPlaylist -> showEmptyPlaylist(state.playlistInfo)
        }
    }

    private fun showFullContent(playlistDetails: PlaylistDetails) {
        showPlaylistInfo(playlistDetails.playlist, playlistDetails.totalDuration)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.isHideable = false
        adapter.submitList(playlistDetails.tracks)
    }

    private fun showEmptyPlaylist(playlistDetails: PlaylistDetails) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.isHideable = true
        showPlaylistInfo(playlistDetails.playlist)
    }

    private fun showPlaylistInfo(playlist: Playlist, totalDuration: Int = 0) {
        with(binding) {
            with(playlist) {
                tvPlaylistDescription.setTextOrHide(text = description, fieldLabel = null)
                tvPlaylistTitle.text = name
                tvTracksTotal.text = resources.getQuantityString(
                    R.plurals.track_amount,
                    numberOfTracks,
                    numberOfTracks
                )
                tvTotalDuration.text = resources.getQuantityString(
                    R.plurals.total_minutes,
                    totalDuration,
                    totalDuration
                )
                Glide.with(ivPlaylistCover)
                    .load(coverUri)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            ivPlaceholder.visibility = View.VISIBLE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    })
                    .into(ivPlaylistCover)
            }
        }
    }

    private fun initBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun initTracksRv() {
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = adapter
    }

    private fun setArrowBackClickListener() {
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