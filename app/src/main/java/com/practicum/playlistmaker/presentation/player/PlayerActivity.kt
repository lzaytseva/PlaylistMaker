package com.practicum.playlistmaker.presentation.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.presentation.model.Track

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
    }

    private fun setTrackInfoToViews() {
        with(binding) {
            Glide.with(this@PlayerActivity)
                .load(track.artworkUrl512)
                .placeholder(R.drawable.album_placeholder_big)
                .transform(RoundedCorners(10))
                .into(ivAlbumCover)
            tvSongTitle.text = track.trackName
            tvArtist.text = track.artistName
            tvDuration.text = track.getDuration()
            if (track.collectionName != null) {
                tvAlbum.text = track.collectionName
            } else {
                tvAlbum.visibility = View.INVISIBLE
                tvAlbumLabel.visibility = View.INVISIBLE
            }
            tvGenre.text = track.primaryGenreName
            tvCountry.text = track.country
            tvYear.text = track.releaseDate
            tvYear.text = track.getYear()
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