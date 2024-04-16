package com.practicum.playlistmaker.player.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.library.playlists.all_playlists.ui.adapters.PlaylistBSAdapter
import com.practicum.playlistmaker.player.domain.model.AddTrackToPlaylistState
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.service.PlayerService
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.BindingFragment
import com.practicum.playlistmaker.util.FeedbackUtils
import com.practicum.playlistmaker.util.NetworkStateBroadcastReceiver
import com.practicum.playlistmaker.util.hideBottomSheet
import com.practicum.playlistmaker.util.setTextOrHide
import com.practicum.playlistmaker.util.showBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.abs

class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapter: PlaylistBSAdapter

    private val networkStateBroadcastReceiver = NetworkStateBroadcastReceiver()

    private var playerService: PlayerService? = null

    private val playerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as PlayerService.PlayerServiceBinder
            playerService = binder.getService()
            playerService?.let {
                viewModel.setPlayerService(it)
                observePlayerState()
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            playerService = null
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            bindMusicService(track)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_notification_permission),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().let {
            track = it.getParcelable(ARGS_TRACK)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissionForService()
        setTrackInfoToViews()
        initPlaylistsRv()
        initBottomSheet()
        setFavsBtnImage(track.isFavorite)
        setArrowBackClickListener()
        setBtnPlayClickListener()
        setAddToFavsClickListener()
        setAddToPlaylistClickListener()
        setBtnCreatePlaylistClickListener()

        observeViewModel()
    }

    private fun bindMusicService(track: Track) {
        requireContext().bindService(
            PlayerService.createIntent(
                requireContext(),
                track.artistName,
                track.trackName,
                track.previewUrl
            ),
            playerServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun requestPermissionForService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            bindMusicService(track)
        }
    }

    override fun onResume() {
        super.onResume()

        ContextCompat.registerReceiver(
            requireContext(),
            networkStateBroadcastReceiver,
            IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        viewModel.hideServiceNotification()
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(networkStateBroadcastReceiver)
        showServiceNotification()
    }

    private fun showServiceNotification() {
        if (playerService?.playerState?.value == PlayerState.PLAYING) {
            viewModel.showServiceNotification()
        }
    }

    private fun observeViewModel() {
        viewModel.isFavorite.observe(viewLifecycleOwner) {
            setFavsBtnImage(it)
        }
        viewModel.addTrackState.observe(viewLifecycleOwner) {
            renderAddTrackState(it)
        }
    }

    private fun observePlayerState() {
        viewModel.playerState?.observe(viewLifecycleOwner) {
            renderPlayerState(it)
        }
        viewModel.timeProgress.observe(viewLifecycleOwner) {
            binding.tvPlayProgress.text = it
        }
    }

    private fun renderPlayerState(state: PlayerState) {
        when (state) {
            // Стейт перед началом и в конце воспроизведения
            PlayerState.PREPARED -> showPlayBtn()
            PlayerState.DEFAULT -> showNotReady()
            PlayerState.ERROR -> showError()
            else -> {}
        }
    }

    private fun showNotReady() {
        binding.btnPlay.setOnClickListener {
            FeedbackUtils.showToast(getString(R.string.player_not_ready), requireContext())
        }
        setIconPlay()
    }

    private fun showPlayBtn() {
        setBtnPlayClickListener()
        setIconPlay()
    }

    private fun setBtnPlayClickListener() {
        binding.btnPlay.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun showError() {
        binding.btnPlay.setOnClickListener {
            FeedbackUtils.showToast(getString(R.string.error_loading_preview), requireContext())
        }
        setIconPlay()
    }

    private fun setIconPlay() {
        binding.btnPlay.setStatePlay()
    }

    private fun setFavsBtnImage(isFavorite: Boolean) {
        if (isFavorite) {
            binding.btnAddToFavs.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_add_to_favs_activated
                )
            )
        } else {
            binding.btnAddToFavs.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_add_to_favs)
            )
        }
    }

    private fun renderAddTrackState(state: AddTrackToPlaylistState) {
        when (state) {
            is AddTrackToPlaylistState.AlreadyPresent -> if (!state.feedbackWasShown) {
                showAlreadyPresent(state)
            }

            is AddTrackToPlaylistState.WasAdded -> if (!state.feedbackWasShown) {
                showTrackAdded(state)
            }

            is AddTrackToPlaylistState.ShowPlaylists -> adapter.submitList(state.playlists)
        }
    }

    private fun showAlreadyPresent(state: AddTrackToPlaylistState.AlreadyPresent) {
        FeedbackUtils.showSnackbar(
            requireView(),
            getString(
                R.string.track_already_present,
                state.playlistName
            )
        )
        viewModel.setFeedbackWasShown(state.copy(feedbackWasShown = true))
    }

    private fun showTrackAdded(state: AddTrackToPlaylistState.WasAdded) {
        bottomSheetBehavior.hideBottomSheet()
        FeedbackUtils.showSnackbar(
            requireView(),
            getString(R.string.track_added_in_playlist, state.playlistName)
        )
        viewModel.setFeedbackWasShown(state.copy(feedbackWasShown = true))
    }

    private fun setBtnCreatePlaylistClickListener() {
        binding.btnCreatePlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
        }
    }

    private fun setAddToPlaylistClickListener() {
        binding.btnAddToPlaylist.setOnClickListener {
            viewModel.getAllPlaylists()
            bottomSheetBehavior.showBottomSheet()
        }
    }

    private fun setAddToFavsClickListener() {
        binding.btnAddToFavs.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
    }

    private fun setArrowBackClickListener() {
        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun initPlaylistsRv() {
        adapter = PlaylistBSAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }
        binding.rvPlaylists.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaylists.adapter = adapter
    }

    private fun setTrackInfoToViews() {
        with(binding) {
            with(track) {
                Glide.with(requireContext())
                    .load(artworkUrl512)
                    .placeholder(R.drawable.album_placeholder_big)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(
                            resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_player)
                        ),
                    )
                    .into(ivAlbumCover)
                tvSongTitle.text = trackName
                tvArtist.text = artistName
                tvDuration.setTextOrHide(duration, tvDurationLabel)
                tvAlbum.setTextOrHide(collectionName, tvAlbumLabel)
                tvGenre.setTextOrHide(primaryGenreName, tvGenreLabel)
                tvCountry.setTextOrHide(country, tvCountryLabel)
                tvYear.setTextOrHide(year, tvYearLabel)
            }
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            hideBottomSheet()
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
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
    }

    override fun onDestroyView() {
        viewModel.hideServiceNotification()
        requireContext().unbindService(playerServiceConnection)
        super.onDestroyView()
    }

    companion object {
        private const val ARGS_TRACK = "track"

        fun createArgs(track: Track): Bundle {
            return bundleOf(
                ARGS_TRACK to track
            )
        }
    }
}