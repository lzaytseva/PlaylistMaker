package com.practicum.playlistmaker.player.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class PlayerService : Service(), AudioPlayerControl {
    private val binder = PlayerServiceBinder()

    private val player: TrackPlayer = get()
    override val playerState = player.playerState

    private val _playbackProgress = MutableStateFlow(INITIAL_PROGRESS)
    override val playbackProgress = _playbackProgress.asStateFlow()

    private var trackUrl: String? = null
    private var trackArtist: String? = null
    private var trackTitle: String? = null

    private var timerJob: Job? = null


    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onBind(p0: Intent?): IBinder {
        trackArtist = p0?.getStringExtra(EXTRA_ARTIST)
        trackTitle = p0?.getStringExtra(EXTRA_TITLE)
        trackUrl = p0?.getStringExtra(EXTRA_SONG_URL)

        if (trackUrl != null) {
            player.preparePlayer(trackUrl!!)
        }

        hideNotificationOnTrackEnded()

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        player.release()
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun playbackControl() {
        when (playerState.value) {
            PlayerState.PLAYING -> {
                player.pause()
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                player.play()
            }

            else -> {}
        }
        updateTimer()
    }

    private fun updateTimer() {
        val time = getCurrentPosition()

        when (playerState.value) {
            PlayerState.PLAYING -> {
                _playbackProgress.value = time
                timerJob = coroutineScope.launch(Dispatchers.Default) {
                    delay(UPDATE_TIMER_DELAY_IN_MILLIS)
                    updateTimer()
                }
            }

            PlayerState.PAUSED -> {
                _playbackProgress.value = time
                timerJob?.cancel()
            }

            else -> {
                timerJob?.cancel()
                _playbackProgress.value = INITIAL_PROGRESS
            }
        }
    }

    private fun getCurrentPosition(): Int {
        return try {
            player.getCurrentPosition()
        } catch (e: IllegalStateException) {
            INITIAL_PROGRESS
        }
    }

    override fun showNotification() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.service_notification, trackArtist, trackTitle))
            .setSmallIcon(R.drawable.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    private fun hideNotificationOnTrackEnded() {
        coroutineScope.launch {
            playerState.collect { state ->
                if (state != PlayerState.PLAYING || state != PlayerState.PAUSED) {
                    hideNotification()
                }
            }
        }
    }

    inner class PlayerServiceBinder : Binder() {
        fun getService(): AudioPlayerControl = this@PlayerService
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "Player Service"
        const val SERVICE_NOTIFICATION_ID = 101
        private const val EXTRA_ARTIST = "EXTRA_ARTIST"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_SONG_URL = "EXTRA_URL"

        private const val UPDATE_TIMER_DELAY_IN_MILLIS = 300L
        private const val INITIAL_PROGRESS = 0

        fun createIntent(
            context: Context,
            artist: String,
            track: String,
            url: String
        ): Intent {
            return Intent(context, PlayerService::class.java).apply {
                putExtra(EXTRA_ARTIST, artist)
                putExtra(EXTRA_TITLE, track)
                putExtra(EXTRA_SONG_URL, url)
            }
        }
    }
}