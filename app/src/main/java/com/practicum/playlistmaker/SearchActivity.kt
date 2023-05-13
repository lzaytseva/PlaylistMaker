package com.practicum.playlistmaker

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

    private val tracksList = ArrayList<Track>()
    private val adapter = TrackAdapter()

    private var savedSearchRequest = ""

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buildRecyclerView()

        binding.searchEditText.setText(savedSearchRequest)

        binding.searchEditText.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnClear.visibility = clearButtonVisibility(s)
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
            keyboard.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0) // скрыть клавиатуру
            binding.searchEditText.clearFocus()

            tracksList.clear()
            adapter.notifyDataSetChanged()
            binding.placeholderError.visibility = View.GONE
        }

        binding.btnRefresh.setOnClickListener {
            search()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun buildRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.tracksList = tracksList
        binding.recyclerView.adapter = adapter
    }

    private fun search() {
        itunesService.search(binding.searchEditText.text.toString())
            .enqueue(object: Callback<SearchTracksResponse> {
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
                    }
                    else {
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
                binding.placeholderMessage.text = getString(R.string.check_connection)
            }
            tracksList.clear()
            adapter.notifyDataSetChanged()
            setPlaceHolderImage(state)
        }
    }

    private fun isNightMode(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun setPlaceHolderImage(state: LoadingState) {
        if (state == LoadingState.NOTHING_FOUND) {
            binding.placeholderImage.setImageResource(
                if (isNightMode()) R.drawable.dark_mode_emtpty
                else R.drawable.light_mode_empty
            )
        }
        else if (state == LoadingState.NO_INTERNET) {
            binding.placeholderImage.setImageResource(
                if (isNightMode()) R.drawable.dark_mode_no_internet
                else R.drawable.light_mode_no_internet
            )
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
    }
}