package com.practicum.playlistmaker.data.storage.shared_prefs

import android.content.SharedPreferences
import com.practicum.playlistmaker.data.mappers.TrackMapper
import com.practicum.playlistmaker.data.storage.HistoryStorage
import com.practicum.playlistmaker.domain.model.Track

class SharedPrefsHistoryStorage(private val sharedPrefs: SharedPreferences) : HistoryStorage {
    private val savedTracks = mutableListOf<Track>()
    private val mapper = TrackMapper()

    init {
        val tracksString = sharedPrefs.getString(HISTORY_LIST_KEY, "")
        if (tracksString?.isNotEmpty() == true) {
            savedTracks.addAll(mapper.createTracksListFromJson(tracksString))
        }
    }

    override fun saveTrack(track: Track) {
        if (savedTracks.contains(track)) {
            savedTracks.remove(track)
        }
        if (savedTracks.size == MAX_NUMBER_OF_TRACKS) {
            savedTracks.removeLast();
        }
        savedTracks.add(0, track)

        sharedPrefs.edit()
            .putString(
                HISTORY_LIST_KEY,
                mapper.createJsonFromTracksList(savedTracks.toTypedArray())
            )
            .apply()
    }

    override fun getAllTracks(): List<Track> {
        return savedTracks.toList()
    }

    override fun clearHistory() {
        savedTracks.clear()
        sharedPrefs.edit()
            .remove(HISTORY_LIST_KEY)
            .apply()
    }

    companion object {
        const val HISTORY_LIST_KEY = "history_list_key"
        const val MAX_NUMBER_OF_TRACKS = 10
    }
}