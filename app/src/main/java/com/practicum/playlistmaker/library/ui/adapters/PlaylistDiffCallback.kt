package com.practicum.playlistmaker.library.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.library.domain.model.Playlist

object PlaylistDiffCallback: DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}