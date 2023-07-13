package com.practicum.playlistmaker.data.storage.model

data class TrackDbModel(
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
)