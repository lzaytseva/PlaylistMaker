package com.practicum.playlistmaker.presentation.search

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.presentation.model.Track

class SearchHistory(private val sharedPrefs: SharedPreferences) {
    val savedTracks = ArrayList<Track>()

    init {
        val tracksString = sharedPrefs.getString(HISTORY_LIST_KEY, "")
        if (tracksString?.isNotEmpty() == true) {
            savedTracks.addAll(createTracksListFromJson(tracksString))
        }
    }

    fun saveTrack(track: Track) {
        if (savedTracks.contains(track)) {
            savedTracks.remove(track)
        }
        if (savedTracks.size == MAX_NUMBER_OF_TRACKS) {
            savedTracks.removeLast();
        }
        savedTracks.add(0, track)

        sharedPrefs.edit()
            .putString(HISTORY_LIST_KEY, createJsonFromTracksList(savedTracks.toTypedArray()))
            .apply()
    }

    fun createTracksListFromJson(json: String): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    private fun createJsonFromTracksList(facts: Array<Track>): String {
        return Gson().toJson(facts)
    }

    fun clearHistory() {
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