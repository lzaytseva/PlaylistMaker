package com.practicum.playlistmaker.library.data.db.repository

import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.mapper.PlaylistDbMapper
import com.practicum.playlistmaker.library.data.db.mapper.TrackDbMapper
import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(
    private val application: Application,
    private val db: AppDatabase,
    private val playlistMapper: PlaylistDbMapper,
    private val trackMapper: TrackDbMapper,
    private val sharedPrefs: SharedPreferences
) : PlaylistsRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        db.playlistsDao().savePlaylist(playlistMapper.mapDomainToEntity(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        emit(playlistMapper.mapEntityListToDomain(db.playlistsDao().getAllPlaylists()))
    }


    override suspend fun updatePlaylist(playlist: Playlist) {
        db.playlistsDao().updateTracksInPlaylist(playlistMapper.mapDomainToEntity(playlist))
    }

    override suspend fun addTrack(track: Track) {
        db.tracksDao().addTrack(trackMapper.mapDomainToPlaylistTrack(track))
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

        var coverNumber = getCoverNumber()

        val file = File(
            filePath, String.format(
                FILE_NAME,
                if (coverNumber == 0) "" else coverNumber
            )
        )

        val inputStream = application.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        setCoverNumber(++coverNumber)

        return Uri.fromFile(file)
    }


    private fun getCoverNumber(): Int {
        return sharedPrefs.getInt(COVERS_COUNT_KEY, 0)
    }

    private fun setCoverNumber(count: Int) {
        sharedPrefs
            .edit()
            .putInt(COVERS_COUNT_KEY, count)
            .apply()
    }


    companion object {
        private const val DIRECTORY_NAME = "playlist_covers"
        private const val FILE_NAME = "playlist_cover%s.jpg"
        private const val COVERS_COUNT_KEY = "covers_count"
    }
}