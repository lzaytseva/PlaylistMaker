package com.practicum.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Int, //уникальный идентификатор
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val duration: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String?,
    val year: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Parcelable {

    val artworkUrl512
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}