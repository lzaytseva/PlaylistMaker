package com.practicum.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.BindingFragment
import com.practicum.playlistmaker.util.setTextOrHide
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireArguments().let {
            track = it.getParcelable(ARGS_TRACK)!!
        }

        setTrackInfoToViews()

        observeViewModel()

        setFavsBtnImage(track.isFavorite)

        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.btnAddToFavs.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(viewLifecycleOwner) {
            renderState(it)
        }
        viewModel.timeProgress.observe(viewLifecycleOwner) {
            binding.tvPlayProgress.text = it
        }
        viewModel.isFavorite.observe(viewLifecycleOwner) {
            setFavsBtnImage(it)
        }
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

    private fun renderState(state: PlayerState) {
        when (state) {
            PlayerState.PLAYING -> showPauseBtn()
            PlayerState.PAUSED, PlayerState.PREPARED -> showPlayBtn()
            PlayerState.DEFAULT -> showNotReady()
            PlayerState.ERROR -> showError()
        }
    }

    private fun showNotReady() {
        binding.btnPlay.setOnClickListener {
            showToast(getString(R.string.player_not_ready))
        }
        setIconPlay()
    }

    private fun showPlayBtn() {
        binding.btnPlay.setOnClickListener {
            viewModel.playbackControl()
        }
        setIconPlay()
    }

    private fun showPauseBtn() {
        binding.btnPlay.setOnClickListener {
            viewModel.playbackControl()
        }
        setIconPause()
    }

    private fun showError() {
        binding.btnPlay.setOnClickListener {
            showToast(getString(R.string.error_loading_preview))
        }
        setIconPlay()
    }

    private fun setIconPause() {
        binding.btnPlay.setImageResource(R.drawable.ic_btn_pause)
    }

    private fun setIconPlay() {
        binding.btnPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
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

    companion object {
        private const val ARGS_TRACK = "track"

        fun createArgs(track: Track): Bundle {
            return bundleOf(
                ARGS_TRACK to track
            )
        }
    }
}