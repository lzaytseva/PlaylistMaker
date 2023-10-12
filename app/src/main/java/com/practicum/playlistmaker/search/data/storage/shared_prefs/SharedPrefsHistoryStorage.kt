package com.practicum.playlistmaker.search.data.storage.shared_prefs

import android.content.SharedPreferences
import com.practicum.playlistmaker.search.data.mappers.TrackMapper
import com.practicum.playlistmaker.search.data.storage.HistoryStorage
import com.practicum.playlistmaker.search.domain.model.Track

class SharedPrefsHistoryStorage(
    private val sharedPrefs: SharedPreferences,
    private val mapper: TrackMapper
) : HistoryStorage {

    private var savedTracks = mutableListOf<Track>()

    override fun saveTrack(track: Track) {
        savedTracks = savedTracks.filterNot {
            it.trackId == track.trackId
        }.toMutableList()

        if (savedTracks.size == MAX_NUMBER_OF_TRACKS) {
            savedTracks.removeLast()
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
        val tracksString = sharedPrefs.getString(HISTORY_LIST_KEY, "")

        if (tracksString?.isNotEmpty() == true) {
            savedTracks.clear()
            savedTracks.addAll(mapper.createTracksListFromJson(tracksString))
        }
        return savedTracks.toList()
    }

    override fun clearHistory() {
        savedTracks.clear()
        sharedPrefs.edit()
            .remove(HISTORY_LIST_KEY)
            .apply()
    }

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val HISTORY_LIST_KEY = "history_list_key"
        const val MAX_NUMBER_OF_TRACKS = 10
    }
}