package com.practicum.playlistmaker.player.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    private val track by lazy {
        intent.extras?.get(EXTRA_KEY_TRACK) as Track
    }

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track.previewUrl)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setTrackInfoToViews()

        observeViewModel()

        binding.arrowBack.setOnClickListener { finish() }

        binding.btnPlay.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(this) {
            renderState(it)
        }
        viewModel.timeProgress.observe(this) {
            binding.tvPlayProgress.text = it
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
        binding.btnPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPlayBtn() {
        binding.btnPlay.setOnClickListener {
            viewModel.playbackControl()
        }
        binding.btnPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPauseBtn() {
        binding.btnPlay.setOnClickListener {
            viewModel.playbackControl()
        }
        binding.btnPlay.setImageResource(R.drawable.ic_btn_pause)
    }

    private fun showError() {
        binding.btnPlay.setOnClickListener {
            showToast(getString(R.string.error_loading_preview))
        }
        binding.btnPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun setTrackInfoToViews() {
        with(binding) {
            with(track) {
                Glide.with(this@PlayerActivity)
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

    private fun TextView.setTextOrHide(text: String, fieldLabel: TextView) {
        if (text.isNotEmpty()) {
            this.text = text
        } else {
            this.visibility = View.GONE
            fieldLabel.visibility = View.GONE
        }
    }

    companion object {
        private const val EXTRA_KEY_TRACK = "extra_key_track"

        fun newIntent(context: Context, track: Track): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_KEY_TRACK, track)
            }
        }
    }
}