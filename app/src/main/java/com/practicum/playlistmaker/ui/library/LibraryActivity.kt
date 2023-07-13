package com.practicum.playlistmaker.ui.library

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.practicum.playlistmaker.R

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LibraryActivity::class.java)
        }
    }
}