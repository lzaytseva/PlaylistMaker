package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.util.ConnectionChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val context: Context,
    private val itunesService: ItunesApi
) : NetworkClient {

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun doRequest(dto: Any): Response {
        if (!ConnectionChecker.isConnected(context)) {
            return Response().apply { resultCode = RC_NO_INTERNET }
        }
        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = RC_WRONG_REQUEST }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = itunesService.search(dto.expression)
                response.apply { resultCode = RC_SUCCESS }
            } catch (e: Throwable) {
                Response().apply { resultCode = RC_FAILURE }
            }
        }
    }

    companion object {
        const val RC_SUCCESS = 200
        const val RC_NO_INTERNET = -1
        const val RC_WRONG_REQUEST = 400
        const val RC_FAILURE = 500

        const val ITUNES_BASE_URL = "https://itunes.apple.com/"
    }
}