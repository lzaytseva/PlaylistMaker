package com.practicum.playlistmaker.library.playlists.playlist_details.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.adapters.TrackAdapter
import com.practicum.playlistmaker.search.ui.adapters.TrackDiffCallback

class PlaylistTrackAdapter(
    private val onTrackClicked: (Track) -> Unit,
    private val onTrackLongClicked: ((Track) -> Boolean)
) : ListAdapter<Track, PlaylistTrackAdapter.PlaylistTrackViewHolder>(TrackDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTrackViewHolder {
        val binding = TrackViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistTrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistTrackViewHolder, position: Int) {
        val track = getItem(holder.adapterPosition)
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onTrackClicked.invoke(track)
        }
        holder.itemView.setOnLongClickListener {
            onTrackLongClicked.invoke(track)
        }
    }

    class PlaylistTrackViewHolder(private val binding: TrackViewBinding) :
        TrackAdapter.TrackViewHolder(binding) {

        override fun setCover(model: Track) {
            Glide.with(itemView)
                .load(model.artworkUrl60)
                .placeholder(R.drawable.cover_placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)
                    ),
                )
                .into(binding.ivAlbumCover)
        }
    }
}
