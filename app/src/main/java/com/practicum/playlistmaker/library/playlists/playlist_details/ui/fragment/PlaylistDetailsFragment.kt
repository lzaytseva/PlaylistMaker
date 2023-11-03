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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
import com.practicum.playlistmaker.library.playlists.all_playlists.ui.adapters.PlaylistBSAdapter
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.model.PlaylistDetails
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.model.PlaylistDetailsScreenState
import com.practicum.playlistmaker.library.playlists.playlist_details.ui.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.player.ui.fragment.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.adapters.TrackAdapter
import com.practicum.playlistmaker.util.BindingFragment
import com.practicum.playlistmaker.util.setTextOrHide
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : BindingFragment<FragmentPlaylistDetailsBinding>() {

    private var playlistId: Int = 0

    private lateinit var bottomSheetBehaviorTracks: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehaviorMore: BottomSheetBehavior<LinearLayout>

    private var playlistAdapter = PlaylistBSAdapter() {}

    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(playlistId)
    }
    private val adapter = TrackAdapter {
        findNavController().navigate(
            R.id.action_playlistDetailsFragment_to_playerFragment,
            PlayerFragment.createArgs(track = it)
        )
    }

    private lateinit var playlist: Playlist

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

        initBottomSheets()
        initTracksRv()
        setArrowBackClickListener()
        setMoreBtnClickListener()
        setClickListenersInMoreBS()
        setShareBtnClickListener()

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
            is PlaylistDetailsScreenState.PlaylistDeleted -> findNavController().navigateUp()
        }
    }

    private fun showFullContent(playlistDetails: PlaylistDetails) {
        showPlaylistInfo(playlistDetails.playlist, playlistDetails.totalDuration)
        //TODO: надо как-то починить болеющую высоту bs для маленьких экранов
        bottomSheetBehaviorTracks.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehaviorTracks.isHideable = false
        adapter.submitList(playlistDetails.tracks)
    }

    private fun showEmptyPlaylist(playlistDetails: PlaylistDetails) {
        bottomSheetBehaviorTracks.isHideable = true
        bottomSheetBehaviorTracks.state = BottomSheetBehavior.STATE_HIDDEN
        adapter.submitList(emptyList())
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
        playlistAdapter.submitList(listOf(playlist))
        this.playlist = playlist
    }

    private fun initBottomSheets() {
        bottomSheetBehaviorTracks = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehaviorMore = BottomSheetBehavior.from(binding.moreBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.rvPlaylist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaylist.adapter = playlistAdapter


    }

    private fun initTracksRv() {
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = adapter
        adapter.setOnTrackLongClicked {
            showDeleteTrackDialog(it)
            true
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(getString(R.string.delete_track_dialog_title))
            .setPositiveButton(getString(R.string.dialog_btn_positive))
            { _, _ ->
                viewModel.deleteTrack(track)
            }
            .setNegativeButton(getString(R.string.dialog_btn_negative))
            { _, _ -> }
            .show()
    }

    private fun setArrowBackClickListener() {
        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setMoreBtnClickListener() {
        binding.btnMore.setOnClickListener {
            bottomSheetBehaviorMore.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setClickListenersInMoreBS() {
        binding.btnShareBs.setOnClickListener {
            sharePlaylist()
        }
        binding.btnEditPlaylist.setOnClickListener {
            editPlaylist()
        }
        binding.btnDeletePlaylist.setOnClickListener {
            deletePlaylist()
        }
    }

    private fun sharePlaylist() {
        viewModel.sharePlaylist()
    }

    private fun editPlaylist() {

    }

    private fun deletePlaylist() {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(getString(R.string.title_delete_playlist_dialog, playlist.name))
            .setPositiveButton(getString(R.string.dialog_btn_positive))
            { _, _ ->
                viewModel.deletePlaylist()
            }
            .setNegativeButton(getString(R.string.dialog_btn_negative))
            { _, _ -> }
            .show()
    }

    private fun setShareBtnClickListener() {
        binding.btnShare.setOnClickListener {
            viewModel.sharePlaylist()
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