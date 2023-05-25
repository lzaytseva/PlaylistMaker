package com.practicum.playlistmaker.track

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R

class TrackAdapter() : RecyclerView.Adapter<TrackViewHolder>() {
    var tracksList = ArrayList<Track>()
    var onTrackClicked: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount() = tracksList.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracksList[position])

        holder.itemView.setOnClickListener {
            onTrackClicked?.invoke(tracksList[position])
        }
    }

}
