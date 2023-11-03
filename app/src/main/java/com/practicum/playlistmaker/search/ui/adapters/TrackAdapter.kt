package com.practicum.playlistmaker.search.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.setTextOrHide

class TrackAdapter(
    private val onTrackClicked: (Track) -> Unit,
) : ListAdapter<Track, TrackAdapter.TrackViewHolder>(TrackDiffCallback) {

    private var onTrackLongClicked: ((Track) -> Boolean)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(holder.adapterPosition)
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onTrackClicked.invoke(track)
        }
        holder.itemView.setOnLongClickListener {
            onTrackLongClicked?.invoke(track) ?: false
        }
    }

    fun setOnTrackLongClicked(onTrackLongClicked: (Track) -> Boolean) {
        this.onTrackLongClicked = onTrackLongClicked
    }

    class TrackViewHolder(private val binding: TrackViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Track) {
            with(binding) {
                with(model) {
                    tvSongTitle.text = trackName
                    tvArtist.text = artistName
                    tvDuration.setTextOrHide(duration, ellipse)
                    Glide.with(itemView)
                        .load(artworkUrl100)
                        .placeholder(R.drawable.cover_placeholder)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(
                                itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)
                            ),
                        )
                        .into(ivAlbumCover)
                }
            }
        }
    }
}
