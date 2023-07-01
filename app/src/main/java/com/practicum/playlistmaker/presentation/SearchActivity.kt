package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var binding: ActivitySearchBinding

    private val itunesService = ApiFactory.itunesService

    private val tracksInHistory = ArrayList<Track>()
    private val tracksList = ArrayList<Track>()
    private val adapter = TrackAdapter()
    private val searchHistoryAdapter = TrackAdapter()

    private var savedSearchRequest = ""

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getHistoryFromSP()

        buildSearchRecyclerView()
        buildHistoryRecyclerView()

        setupSearchEditText()

        setupBtnClearSearchClickListener()

        binding.btnRefresh.setOnClickListener {
            search()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        if (tracksInHistory.isNotEmpty()) {
            binding.viewGroupHistorySearch.visibility = View.VISIBLE
        }

        setupSPChangeListener()

        setupBtnClearHistoryClickListener()
    }

    private fun setupBtnClearHistoryClickListener() {
        binding.btnClearHistory.setOnClickListener {
            searchHistory.clearHistory()
            tracksInHistory.clear()
            binding.viewGroupHistorySearch.visibility = View.GONE
            searchHistoryAdapter.notifyDataSetChanged()
        }
    }

    private fun setupSPChangeListener() {
        listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
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
    }

    private fun setupBtnClearSearchClickListener() {
        binding.btnClear.setOnClickListener {
            binding.searchEditText.setText("")
            savedSearchRequest = ""

            val keyboard = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(
                binding.searchEditText.windowToken, 0
            )
            binding.searchEditText.clearFocus()

            tracksList.clear()
            adapter.notifyDataSetChanged()

            binding.placeholderError.visibility = View.GONE

            if (tracksInHistory.isNotEmpty()) {
                binding.viewGroupHistorySearch.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSearchEditText() {
        binding.searchEditText.setText(savedSearchRequest)
        setupFocusChangeListener()
        setupTextChangedListener()
        setupEditorActionListener()
    }

    private fun setupEditorActionListener() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
    }

    private fun setupTextChangedListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnClear.visibility = clearButtonVisibility(s)
                if (s != null) {
                    binding.viewGroupHistorySearch.visibility =
                        if (binding.searchEditText.hasFocus() && s.isEmpty() && tracksInHistory.isNotEmpty()) View.VISIBLE
                        else View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                savedSearchRequest = s.toString()
            }
        })
    }

    private fun setupFocusChangeListener() {
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            binding.viewGroupHistorySearch.visibility =
                if (hasFocus && binding.searchEditText.text.isEmpty() && tracksInHistory.isNotEmpty()) View.VISIBLE
                else View.GONE
        }
    }

    private fun getHistoryFromSP() {
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        tracksInHistory.addAll(searchHistory.savedTracks)
    }

    private fun buildSearchRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.tracksList = tracksList
        adapter.onTrackClicked = { track: Track ->
            searchHistory.saveTrack(track)
            PlayerActivity.newIntent(this, track).apply { startActivity(this) }
        }
        binding.recyclerView.adapter = adapter
    }

    private fun buildHistoryRecyclerView() {
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        searchHistoryAdapter.tracksList = tracksInHistory
        binding.recyclerViewHistory.adapter = searchHistoryAdapter
        searchHistoryAdapter.onTrackClicked = {
            PlayerActivity.newIntent(this, it).apply {
                startActivity(this)
            }
        }
    }

    private fun search() {
        itunesService.search(binding.searchEditText.text.toString())
            .enqueue(object : Callback<SearchTracksResponse> {
                override fun onResponse(
                    call: Call<SearchTracksResponse>, response: Response<SearchTracksResponse>
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
        const val SEARCH_ET_TEXT = "search_et_text"
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val HISTORY_LIST_KEY = "history_list_key"

        fun newIntent(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }
    }

    enum class LoadingState {
        SUCCESS, NO_INTERNET, NOTHING_FOUND
    }
}