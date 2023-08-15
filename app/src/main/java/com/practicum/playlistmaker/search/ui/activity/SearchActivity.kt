package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.domain.model.SearchActivityState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.adapters.TrackAdapter
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private val viewModel: SearchViewModel by viewModel()
    private val tracksInHistory = ArrayList<Track>()
    private val tracksList = ArrayList<Track>()
    private lateinit var adapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter

    private var savedSearchRequest = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initAdapters()
        buildSearchRecyclerView()
        buildHistoryRecyclerView()

        setupSearchEditText()
        setupBtnClearSearchClickListener()
        setupBtnClearHistoryClickListener()
        setupBtnRefreshClickListener()
        setupBtnBackClickListener()

        viewModel.state.observe(this) {
            renderState(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.searchEditText.text.isEmpty()) {
            viewModel.showHistory()
        }
    }

    private fun renderState(it: SearchActivityState) {
        when (it) {
            is SearchActivityState.Loading -> showLoading()
            is SearchActivityState.Content -> showFoundTracks(it.tracks)
            is SearchActivityState.Empty -> showEmpty(it.message)
            is SearchActivityState.Error -> showError(it.errorMessage)
            is SearchActivityState.SearchHistory -> showHistory(it.tracks)
        }
    }

    private fun showHistory(tracks: List<Track>) {
        tracksInHistory.clear()
        tracksInHistory.addAll(tracks)
        binding.placeholderError.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        if (tracksInHistory.isNotEmpty()) {
            binding.viewGroupHistorySearch.visibility = View.VISIBLE
            searchHistoryAdapter.notifyDataSetChanged()
        }
    }

    private fun showError(errorMessage: String) {
        binding.viewGroupHistorySearch.visibility = View.GONE
        binding.placeholderError.visibility = View.VISIBLE
        binding.btnRefresh.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.placeholderMessage.text = errorMessage
        binding.placeholderImage.setImageResource(R.drawable.ic_no_internet)
    }

    private fun showEmpty(message: String) {
        binding.viewGroupHistorySearch.visibility = View.GONE
        binding.placeholderError.visibility = View.VISIBLE
        binding.btnRefresh.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.placeholderMessage.text = message
        binding.placeholderImage.setImageResource(R.drawable.ic_empty)
    }

    private fun showFoundTracks(foundTracks: List<Track>) {
        binding.viewGroupHistorySearch.visibility = View.GONE
        binding.placeholderError.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        tracksList.clear()
        tracksList.addAll(foundTracks)
        adapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        binding.recyclerView.visibility = View.GONE
        binding.viewGroupHistorySearch.visibility = View.GONE
        binding.placeholderError.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun initAdapters() {
        val itemClickListener: (Track) -> Unit = {
            if (viewModel.clickDebounce()) {
                viewModel.saveTrack(it)
                PlayerActivity.newIntent(this, it).apply {
                    startActivity(this)
                }
            }
        }
        adapter = TrackAdapter(itemClickListener)
        searchHistoryAdapter = TrackAdapter(itemClickListener)
    }

    private fun setupBtnClearHistoryClickListener() {
        binding.btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
            binding.viewGroupHistorySearch.visibility = View.GONE
            searchHistoryAdapter.notifyDataSetChanged()
        }
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
                    search()
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

    private fun buildSearchRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.tracksList = tracksList
        binding.recyclerView.adapter = adapter
    }

    private fun buildHistoryRecyclerView() {
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        searchHistoryAdapter.tracksList = tracksInHistory
        binding.recyclerViewHistory.adapter = searchHistoryAdapter
    }

    private fun search() {
        viewModel.searchDebounce(binding.searchEditText.text.toString())
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun setupBtnBackClickListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupBtnRefreshClickListener() {
        binding.btnRefresh.setOnClickListener {
            search()
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

        fun newIntent(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }
    }
}