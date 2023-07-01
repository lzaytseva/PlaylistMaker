package com.practicum.playlistmaker.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.model.Track

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
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

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songTitle: TextView = itemView.findViewById(R.id.tv_song_title)
        private val artist: TextView = itemView.findViewById(R.id.tv_artist)
        private val duration: TextView = itemView.findViewById(R.id.tv_duration)
        private val albumCover: ImageView = itemView.findViewById(R.id.iv_album_cover)

        fun bind(model: Track) {
            songTitle.text = model.trackName
            artist.text = model.artistName
            duration.text = model.getDuration()
            Glide.with(itemView)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.album_placeholder)
                .transform(RoundedCorners(10))
                .into(albumCover)
        }
    }
}
