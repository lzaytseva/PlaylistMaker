package com.practicum.playlistmaker.search.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val context: Context,
    private val itunesService: ItunesApi
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
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

    @SuppressLint("NewApi")
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    companion object {
        const val RC_SUCCESS = 200
        const val RC_NO_INTERNET = -1
        const val RC_WRONG_REQUEST = 400
        const val RC_FAILURE = 500

        const val ITUNES_BASE_URL = "https://itunes.apple.com/"
    }
}