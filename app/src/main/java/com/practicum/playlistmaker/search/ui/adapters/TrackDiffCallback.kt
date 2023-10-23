package com.practicum.playlistmaker.search.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.search.domain.model.Track

object TrackDiffCallback : DiffUtil.ItemCallback<Track>() {

    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.trackId == newItem.trackId
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}