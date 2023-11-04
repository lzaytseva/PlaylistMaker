package com.practicum.playlistmaker.util

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R

object FeedbackUtils {
    fun showSnackbar(root: View, text: String) {
        val snackbar =
            Snackbar.make(root, text, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(
            ContextCompat.getColor(
                root.context,
                R.color.basic_btn_background
            )
        )
        snackbar.setTextColor(
            ContextCompat.getColor(
                root.context,
                R.color.basic_btn_text_color
            )
        )
        val textView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackbar.show()
    }
}