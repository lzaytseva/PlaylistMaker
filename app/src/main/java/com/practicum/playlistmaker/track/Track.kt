package com.practicum.playlistmaker.track

data class Track(
    val trackId: Int, //уникальный идентификатор
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложк
)