package com.practicum.playlistmaker.library.playlists.all_playlists.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist

class PlaylistAdapter :
    ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(PlaylistDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaylistViewHolder(private val binding: PlaylistViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            with(binding) {
                with(playlist) {
                    tvPlaylistTitle.text = name
                    val numOfTracks = itemView.resources.getQuantityString(
                        R.plurals.track_amount,
                        numberOfTracks,
                        numberOfTracks
                    )
                    tvNumOfTracks.text = numOfTracks
                    Glide.with(itemView)
                        .load(coverUri)
                        .placeholder(R.drawable.cover_placeholder)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(
                                itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_player)
                            ),
                        )
                        .into(ivPlaylistCover)
                }
            }
        }
    }
}