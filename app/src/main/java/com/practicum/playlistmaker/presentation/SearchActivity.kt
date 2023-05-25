package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SearchHistory
import com.practicum.playlistmaker.api.ApiFactory
import com.practicum.playlistmaker.api.SearchTracksResponse
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.track.Track
import com.practicum.playlistmaker.track.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private val itunesService = ApiFactory.itunesService

    private val tracksInHistory = ArrayList<Track>()
    private val tracksList = ArrayList<Track>()
    private val adapter = TrackAdapter()
    private val searchHistoryAdapter = TrackAdapter()

    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    private var savedSearchRequest = ""

    private lateinit var binding: ActivitySearchBinding

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        tracksInHistory.addAll(searchHistory.savedTracks)

        buildSearchRecyclerView()
        buildHistoryRecyclerView()

        binding.searchEditText.setText(savedSearchRequest)

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            binding.viewGroupHistorySearch.visibility =
                if (hasFocus && binding.searchEditText.text.isEmpty() && tracksInHistory.isNotEmpty())
                    View.VISIBLE
                else
                    View.GONE
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnClear.visibility = clearButtonVisibility(s)
                if (s != null) {
                    binding.viewGroupHistorySearch.visibility =
                        if (binding.searchEditText.hasFocus() && s.isEmpty() && tracksInHistory.isNotEmpty())
                            View.VISIBLE
                        else
                            View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
                savedSearchRequest = s.toString()
            }
        })

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        binding.btnClear.setOnClickListener {
            binding.searchEditText.setText("")
            savedSearchRequest = ""

            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(
                binding.searchEditText.windowToken,
                0
            ) // скрыть клавиатуру
            binding.searchEditText.clearFocus()

            tracksList.clear()
            adapter.notifyDataSetChanged()

            binding.placeholderError.visibility = View.GONE

            if (tracksInHistory.isNotEmpty()) {
                binding.viewGroupHistorySearch.visibility = View.VISIBLE
            }
        }

        binding.btnRefresh.setOnClickListener {
            search()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }


        if (tracksInHistory.isNotEmpty()) {
            binding.viewGroupHistorySearch.visibility = View.VISIBLE
        }


        listener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == HISTORY_LIST_KEY) {
                    val history = sharedPreferences?.getString(HISTORY_LIST_KEY, null)
                    if (history != null) {
                        tracksInHistory.clear()
                        tracksInHistory.addAll(searchHistory.createTracksListFromJson(history))
                        searchHistoryAdapter.notifyDataSetChanged()
                    }
                }
            }

        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

        binding.btnClearHistory.setOnClickListener {
            searchHistory.clearHistory()
            tracksInHistory.clear()
            binding.viewGroupHistorySearch.visibility = View.GONE
            searchHistoryAdapter.notifyDataSetChanged()
        }

    }


    private fun buildSearchRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.tracksList = tracksList
        adapter.onTrackClicked = { track: Track -> searchHistory.saveTrack(track) }
        binding.recyclerView.adapter = adapter
    }

    private fun buildHistoryRecyclerView() {
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        searchHistoryAdapter.tracksList = tracksInHistory
        binding.recyclerViewHistory.adapter = searchHistoryAdapter
    }

    private fun search() {
        itunesService.search(binding.searchEditText.text.toString())
            .enqueue(object : Callback<SearchTracksResponse> {
                override fun onResponse(
                    call: Call<SearchTracksResponse>,
                    response: Response<SearchTracksResponse>
                ) {
                    if (response.code() == 200) {
                        tracksList.clear()
                        if (response.body()?.tracks?.isNotEmpty() == true) {
                            tracksList.addAll(response.body()?.tracks!!)
                            showResult(LoadingState.SUCCESS)
                        }
                        if (tracksList.isEmpty()) {
                            showResult(LoadingState.NOTHING_FOUND)
                        }
                    } else {
                        showResult(LoadingState.NO_INTERNET)
                    }
                }

                override fun onFailure(call: Call<SearchTracksResponse>, t: Throwable) {
                    showResult(LoadingState.NO_INTERNET)
                }
            })
    }

    private fun showResult(state: LoadingState) {
        if (state == LoadingState.SUCCESS) {
            adapter.notifyDataSetChanged()
            binding.placeholderError.visibility = View.GONE
        } else {
            binding.placeholderError.visibility = View.VISIBLE
            if (state == LoadingState.NOTHING_FOUND) {
                binding.btnRefresh.visibility = View.INVISIBLE
                binding.placeholderMessage.text = getString(R.string.nothing_found)
            } else if (state == LoadingState.NO_INTERNET) {
                binding.btnRefresh.visibility = View.VISIBLE
                binding.placeholderMessage.text = getString(R.string.check_connection)
            }
            tracksList.clear()
            adapter.notifyDataSetChanged()
            setPlaceHolderImage(state)
        }
    }

    private fun setPlaceHolderImage(state: LoadingState) {
        if (state == LoadingState.NOTHING_FOUND) {
            binding.placeholderImage.setImageResource(R.drawable.ic_empty)
        } else if (state == LoadingState.NO_INTERNET) {
            binding.placeholderImage.setImageResource(R.drawable.ic_no_internet)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedSearchRequest = savedInstanceState.getString(SEARCH_ET_TEXT, "")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_ET_TEXT, savedSearchRequest)
    }

    companion object {
        const val SEARCH_ET_TEXT = "SEARCH_ET_TEXT"
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val HISTORY_LIST_KEY = "history_list_key"
    }

    enum class LoadingState {
        SUCCESS,
        NO_INTERNET,
        NOTHING_FOUND
    }
}