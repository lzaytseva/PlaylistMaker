package com.practicum.playlistmaker

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.api.ItunesApi
import com.practicum.playlistmaker.api.SearchTracksResponse
import com.practicum.playlistmaker.track.Track
import com.practicum.playlistmaker.track.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val BASE_URL = "https://itunes.apple.com/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val itunesService: ItunesApi = retrofit.create(ItunesApi::class.java)

    private val tracksList = ArrayList<Track>()
    private val adapter = TrackAdapter()

    var savedSearchRequest = ""
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderError: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initViews()
        buildRecyclerView()

        searchEditText.setText(savedSearchRequest)

        searchEditText.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
                savedSearchRequest = s.toString()
            }

        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            savedSearchRequest = ""

            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(searchEditText.windowToken, 0) // скрыть клавиатуру
            searchEditText.clearFocus()

            tracksList.clear()
            adapter.notifyDataSetChanged()
        }

        refreshButton.setOnClickListener {
            search()
        }

    }

    private fun initViews() {
        clearButton = findViewById(R.id.btn_clear)
        searchEditText = findViewById(R.id.search_edit_text)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        placeholderError = findViewById(R.id.placeHolderError)
        placeholderImage = findViewById(R.id.placeHolderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        refreshButton = findViewById(R.id.button_refresh)
    }

    private fun buildRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.tracksList = tracksList
        recyclerView.adapter = adapter
    }

    private fun search() {
        itunesService.search(searchEditText.text.toString())
            .enqueue(object: Callback<SearchTracksResponse> {
                override fun onResponse(
                    call: Call<SearchTracksResponse>,
                    response: Response<SearchTracksResponse>
                ) {
                    if (response.code() == 200) {
                        tracksList.clear()
                        if (response.body()?.tracks?.isNotEmpty() == true) {
                            tracksList.addAll(response.body()?.tracks!!)
                            adapter.notifyDataSetChanged()
                        }
                        if (tracksList.isEmpty()) {
                            showMessage(getString(R.string.nothing_found))

                        } else {
                            placeholderError.visibility = View.GONE
                        }
                    }
                    else {
                        showMessage(getString(R.string.check_connection))
                    }
                }

                override fun onFailure(call: Call<SearchTracksResponse>, t: Throwable) {
                    showMessage(getString(R.string.check_connection))
                }

            })
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            placeholderError.visibility = View.VISIBLE
            tracksList.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            setPlaceHolderImage(text)
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    private fun setPlaceHolderImage(text: String) {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                if (text.equals(getString(R.string.nothing_found))) {
                    placeholderImage.setImageResource(R.drawable.light_mode_empty)
                } else {
                    placeholderImage.setImageResource(R.drawable.light_mode_no_internet)
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                if (text.equals(getString(R.string.nothing_found))) {
                    placeholderImage.setImageResource(R.drawable.dark_mode_emtpty)
                } else {
                    placeholderImage.setImageResource(R.drawable.dark_mode_no_internet)
                }
            }
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