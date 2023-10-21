package com.practicum.playlistmaker.library.data.db.repository

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.mapper.PlaylistDbMapper
import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(
    private val application: Application,
    private val db: AppDatabase,
    private val mapper: PlaylistDbMapper
) : PlaylistsRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        db.playlistsDao().savePlaylist(mapper.mapDomainToEntity(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        emit(mapper.mapEntityListToDomain(db.playlistsDao().getAllPlaylists()))
    }

    override fun saveCoverToStorage(uri: Uri?): Uri {
        if (uri == null) {
            return Uri.EMPTY
        }

        val filePath = File(
            application.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            DIRECTORY_NAME
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        //дичь какая-то
        var suffix = filePath.listFiles()?.last()?.name
            ?.substringAfter("playlist_cover")
            ?.substringBefore(".jpg")
            ?.toInt() ?: 0

        val file = File(filePath, String.format(FILE_NAME, ++suffix))

        val inputStream = application.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.absolutePath.toUri()
    }

    companion object {
        private const val DIRECTORY_NAME = "playlist_covers"
        private const val FILE_NAME = "playlist_cover%d.jpg"
    }
}