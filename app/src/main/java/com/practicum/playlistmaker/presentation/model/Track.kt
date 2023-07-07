package com.practicum.playlistmaker.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class Track(
    val trackId: Int, //уникальный идентификатор
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Parcelable {

    val artworkUrl512
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    fun getDuration(): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    fun getYear() = releaseDate.substringBefore("-")
}