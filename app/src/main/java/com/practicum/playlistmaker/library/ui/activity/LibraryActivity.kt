package com.practicum.playlistmaker.library.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityLibraryBinding
import com.practicum.playlistmaker.library.ui.adapters.LibraryPagerAdapter

class LibraryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLibraryBinding.inflate(layoutInflater)
    }

    private lateinit var tabMediator: TabLayoutMediator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.viewPager.adapter = LibraryPagerAdapter(supportFragmentManager, lifecycle)
        attachTabMediator()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun attachTabMediator() {
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favourite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LibraryActivity::class.java)
        }
    }
}