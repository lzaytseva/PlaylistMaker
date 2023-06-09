package com.practicum.playlistmaker.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.presentation.search.SearchActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener {
            val searchIntent = SearchActivity.newIntent(this)
            startActivity(searchIntent)
        }

        binding.btnLibrary.setOnClickListener {
            val libraryIntent = LibraryActivity.newIntent(this)
            startActivity(libraryIntent)
        }

        binding.btnSettings.setOnClickListener {
            val settingsIntent = SettingsActivity.newIntent(this)
            startActivity(settingsIntent)
        }
    }
}