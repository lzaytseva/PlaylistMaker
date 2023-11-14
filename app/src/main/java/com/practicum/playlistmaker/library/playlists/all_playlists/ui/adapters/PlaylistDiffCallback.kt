package com.practicum.playlistmaker.library.playlists.all_playlists.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist

object PlaylistDiffCallback: DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}