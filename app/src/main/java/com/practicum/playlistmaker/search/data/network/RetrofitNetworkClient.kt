package com.practicum.playlistmaker.search.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val context: Context): NetworkClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService: ItunesApi = retrofit.create(ItunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }
        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = 400 }
        }

        val response = itunesService.search(dto.expression).execute()
        val body = response.body()
        return body?.apply { resultCode = response.code() } ?: Response().apply { resultCode = response.code() }
    }

    @SuppressLint("NewApi")
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
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
        private const val ITUNES_BASE_URL = "https://itunes.apple.com/"
    }

}