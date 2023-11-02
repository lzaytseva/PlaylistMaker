package com.practicum.playlistmaker.library.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.library.ui.fragment.FavouriteTracksFragment
import com.practicum.playlistmaker.library.ui.fragment.PlaylistsFragment

class LibraryPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavouriteTracksFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }
}