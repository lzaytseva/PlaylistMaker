package com.practicum.playlistmaker.util

import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun BottomSheetBehavior<LinearLayout>.hideBottomSheet() {
    state = BottomSheetBehavior.STATE_HIDDEN
}

fun BottomSheetBehavior<LinearLayout>.showBottomSheet() {
    state = BottomSheetBehavior.STATE_COLLAPSED
}

