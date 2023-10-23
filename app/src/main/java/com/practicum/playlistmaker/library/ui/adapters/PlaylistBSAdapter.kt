package com.practicum.playlistmaker.library.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistBotomsheetViewBinding
import com.practicum.playlistmaker.library.domain.model.Playlist

class PlaylistBSAdapter(
    private val onPlaylistClicked: (Playlist) -> Unit
) :
    ListAdapter<Playlist, PlaylistBSAdapter.PlaylistBSViewHolder>(PlaylistDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBSViewHolder {
        val binding = PlaylistBotomsheetViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistBSViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistBSViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onPlaylistClicked.invoke(playlist)
        }
    }

    class PlaylistBSViewHolder(private val binding: PlaylistBotomsheetViewBinding) :
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
                            RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)),
                        )
                        .into(ivPlaylistCover)
                }
            }
        }
    }
}