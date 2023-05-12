package com.practicum.playlistmaker.track


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.*

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val songTitle: TextView = itemView.findViewById(R.id.tv_song_title)
    private val artist: TextView = itemView.findViewById(R.id.tv_artist)
    private val duration: TextView = itemView.findViewById(R.id.tv_duration)
    private val albumCover: ImageView = itemView.findViewById(R.id.iv_album_cover)

    fun bind(model: Track) {
        songTitle.text = model.trackName
        artist.text = model.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.album_placeholder)
            .transform(RoundedCorners(10))
            .into(albumCover)
    }
}