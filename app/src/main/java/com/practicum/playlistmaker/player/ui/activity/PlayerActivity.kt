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
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModelFactory

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var track: Track

    private lateinit var viewModel: PlayerViewModel
    private lateinit var playerViewModelFactory: PlayerViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = intent.extras?.get(EXTRA_KEY_TRACK) as Track
        setTrackInfoToViews()

        playerViewModelFactory = PlayerViewModelFactory(track.previewUrl)
        viewModel = ViewModelProvider(this, playerViewModelFactory)[PlayerViewModel::class.java]

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
        viewModel.isPlaying.observe(this) {
            val btnImageId =
                if (it) {
                    R.drawable.ic_btn_pause
                } else {
                    R.drawable.ic_play
                }
            binding.btnPlay.setImageResource(btnImageId)
        }
        viewModel.timeProgress.observe(this) {
            binding.tvPlayProgress.text = it
        }
        viewModel.noPreview.observe(this) {
            if (it == true) {
                binding.btnPlay.setOnClickListener {
                    Toast.makeText(
                        this,
                        getString(R.string.no_preview_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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