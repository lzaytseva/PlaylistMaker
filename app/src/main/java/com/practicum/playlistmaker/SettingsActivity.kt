package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ImageView>(R.id.arrow_back)
        btnBack.setOnClickListener {
            val mainScreenIntent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(mainScreenIntent)
        }
    }
}