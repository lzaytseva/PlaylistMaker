package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.library.ui.activity.LibraryActivity
import com.practicum.playlistmaker.search.ui.activity.SearchActivity
import com.practicum.playlistmaker.settings.ui.activity.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener {
            SearchActivity.newIntent(this).apply {
                startActivity(this)
            }

        }

        binding.btnLibrary.setOnClickListener {
            LibraryActivity.newIntent(this).apply {
                startActivity(this)
            }
        }

        binding.btnSettings.setOnClickListener {
            SettingsActivity.newIntent(this).apply {
                startActivity(this)
            }
        }
    }
}