package com.practicum.playlistmaker.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi

class NetworkStateBroadcastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.net.conn.CONNECTIVITY_CHANGE") {
            val isConnected = context?.let { ConnectionChecker.isConnected(it) } ?: false
            if (!isConnected) {
                Toast.makeText(
                    context,
                    "Отсутствует подключение к интернету",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}