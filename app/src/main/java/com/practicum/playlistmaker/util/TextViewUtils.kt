package com.practicum.playlistmaker.util

import android.view.View
import android.widget.TextView

fun TextView.setTextOrHide(text: String, fieldLabel: View?) {
    if (text.isNotEmpty()) {
        this.text = text
        fieldLabel?.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
        fieldLabel?.visibility = View.GONE
    }
}