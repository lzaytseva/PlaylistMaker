package com.practicum.playlistmaker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.presentation.model.Track

class TrackAdapter(private val onTrackClicked: (Track) -> Unit) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    var tracksList = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding)
    }

    override fun getItemCount() = tracksList.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracksList[position])

        holder.itemView.setOnClickListener {
            onTrackClicked.invoke(tracksList[position])
        }
    }

    class TrackViewHolder(private val binding: TrackViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Track) {
            with(binding) {
                with(model) {
                    tvSongTitle.text = trackName
                    tvArtist.text = artistName
                    tvDuration.text = this.getDuration()
                    Glide.with(itemView)
                        .load(artworkUrl100)
                        .placeholder(R.drawable.album_placeholder)
                        .transform(RoundedCorners(10))
                        .into(ivAlbumCover)
                }
            }
        }
    }
}
