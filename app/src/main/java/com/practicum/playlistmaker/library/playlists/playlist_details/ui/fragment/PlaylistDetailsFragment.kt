package com.practicum.playlistmaker.library.playlists.playlist_details.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
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
import com.practicum.playlistmaker.library.playlists.edit_playlist.ui.fragment.EditPlaylistFragment
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.model.PlaylistDetails
import com.practicum.playlistmaker.library.playlists.playlist_details.domain.model.PlaylistDetailsScreenState
import com.practicum.playlistmaker.library.playlists.playlist_details.ui.adapter.PlaylistTrackAdapter
import com.practicum.playlistmaker.library.playlists.playlist_details.ui.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.player.ui.fragment.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.FeedbackUtils
import com.practicum.playlistmaker.util.hideBottomSheet
import com.practicum.playlistmaker.util.showBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.abs

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private var playlistId: Int = 0
    private lateinit var playlist: Playlist

    private lateinit var bottomSheetBehaviorTracks: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehaviorMore: BottomSheetBehavior<LinearLayout>

    private var playlistAdapter = PlaylistBSAdapter {}
    private val adapter = PlaylistTrackAdapter(
        onTrackClicked = {
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_playerFragment,
                PlayerFragment.createArgs(track = it)
            )
        },
        onTrackLongClicked = {
            showDeleteTrackDialog(it)
            true
        }
    )

    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(playlistId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().let {
            playlistId = it.getInt(ARGS_PLAYLIST_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        binding.root.doOnNextLayout {
            calculatePeekHeight()
        }
        return binding.root
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

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylistDetails()
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
            is PlaylistDetailsScreenState.NoApplicationFound -> if (!state.feedbackWasShown) showNoApplicationFound()
            is PlaylistDetailsScreenState.NothingToShare -> if (!state.feedbackWasShown) showNothingToShare()
        }
    }

    private fun showFullContent(playlistDetails: PlaylistDetails) {
        binding.placeholderMessage.visibility = View.INVISIBLE
        showPlaylistInfo(playlistDetails.playlist, playlistDetails.totalDuration)
        bottomSheetBehaviorTracks.showBottomSheet()
        bottomSheetBehaviorTracks.isHideable = false
        adapter.submitList(playlistDetails.tracks)
    }

    private fun showEmptyPlaylist(playlistDetails: PlaylistDetails) {
        binding.placeholderMessage.visibility = View.VISIBLE
        bottomSheetBehaviorTracks.isHideable = true
        bottomSheetBehaviorTracks.hideBottomSheet()
        adapter.submitList(emptyList())
        showPlaylistInfo(playlistDetails.playlist)
    }

    private fun showPlaylistInfo(playlist: Playlist, totalDuration: Int = 0) {
        with(binding) {
            with(playlist) {
                tvPlaylistDescription.text = description
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
                            ivPlaceholder.visibility = View.INVISIBLE
                            return false
                        }
                    })
                    .into(ivPlaylistCover)
            }
        }
        playlistAdapter.submitList(listOf(playlist))
        this.playlist = playlist
    }

    private fun showNoApplicationFound() {
        FeedbackUtils.showToast(getString(R.string.no_applications_found), requireContext())
        viewModel.setFeedbackWasShown(
            state = PlaylistDetailsScreenState.NoApplicationFound(
                feedbackWasShown = true
            )
        )
    }

    private fun showNothingToShare() {
        FeedbackUtils.showSnackbar(
            requireView(),
            getString(R.string.empty_playlist_nothing_to_share)
        )
        viewModel.setFeedbackWasShown(
            state = PlaylistDetailsScreenState.NothingToShare(
                feedbackWasShown = true
            )
        )
    }

    private fun calculatePeekHeight() {
        val screenHeight = binding.root.height
        bottomSheetBehaviorMore.peekHeight =
            (screenHeight - binding.tvPlaylistTitle.bottom).coerceAtLeast(
                BS_MIN_SIZE_PX
            )
        bottomSheetBehaviorTracks.peekHeight =
            (screenHeight - binding.btnShare.bottom - BS_TRACKS_OFFSET_PX).coerceAtLeast(
                BS_MIN_SIZE_PX
            )
        bottomSheetBehaviorTracks.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun initBottomSheets() {
        bottomSheetBehaviorTracks = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            hideBottomSheet()
        }
        bottomSheetBehaviorMore = BottomSheetBehavior.from(binding.moreBottomSheet).apply {
            hideBottomSheet()
        }

        bottomSheetBehaviorMore.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = 1 - abs(slideOffset)
            }
        })

        binding.rvPlaylist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaylist.adapter = playlistAdapter
    }

    private fun initTracksRv() {
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = adapter
        binding.rvTracks.itemAnimator = null
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
            bottomSheetBehaviorMore.showBottomSheet()
        }
    }

    private fun setClickListenersInMoreBS() {
        binding.btnShareBs.setOnClickListener {
            bottomSheetBehaviorMore.hideBottomSheet()
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
        viewModel.sharePlaylist(requireContext())
    }

    private fun editPlaylist() {
        findNavController().navigate(
            R.id.action_playlistDetailsFragment_to_editPlaylistFragment,
            EditPlaylistFragment.createArgs(playlistId)
        )
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
            sharePlaylist()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val BS_TRACKS_OFFSET_PX = 24
        private const val BS_MIN_SIZE_PX = 100
        private const val ARGS_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Int): Bundle {
            return bundleOf(
                ARGS_PLAYLIST_ID to playlistId
            )
        }
    }
}