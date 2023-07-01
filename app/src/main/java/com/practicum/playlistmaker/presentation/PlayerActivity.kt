package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.track.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = intent.extras?.get(EXTRA_KEY_TRACK) as Track
        setTrackInfoToViews()

        binding.arrowBack.setOnClickListener { finish() }
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