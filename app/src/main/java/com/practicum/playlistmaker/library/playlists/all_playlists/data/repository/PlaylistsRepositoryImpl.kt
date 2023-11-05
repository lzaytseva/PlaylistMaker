package com.practicum.playlistmaker.library.playlists.all_playlists.data.repository

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.library.core.data.db.AppDatabase
import com.practicum.playlistmaker.library.fav_tracks.data.mapper.TrackDbMapper
import com.practicum.playlistmaker.library.playlists.all_playlists.data.mapper.PlaylistDbMapper
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.library.playlists.all_playlists.domain.model.Playlist
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


        if (!uri.scheme.equals("content")) {
            return uri
        }

        val file = File(
            filePath, String.format(
                FILE_NAME,
                System.currentTimeMillis()
            )
        )

        val inputStream = application.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return Uri.fromFile(file)
    }

    companion object {
        private const val DIRECTORY_NAME = "playlist_covers"
        private const val FILE_NAME = "playlist_cover%s.jpg"
    }
}