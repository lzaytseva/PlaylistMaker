package com.practicum.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.practicum.playlistmaker.library.ui.FavTracksState
import com.practicum.playlistmaker.library.ui.view_model.FavouriteTracksViewModel
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.adapters.TrackAdapter
import com.practicum.playlistmaker.search.ui.fragment.SearchFragment
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment : Fragment() {

    private var _binding: FragmentFavouriteTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouriteTracksViewModel by viewModel()

    private val favTracks = mutableListOf<Track>()
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        binding.favTracksRv.layoutManager = LinearLayoutManager(requireContext())
        adapter.tracksList = favTracks
        viewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    private fun initAdapter() {
        val onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_IN_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            PlayerActivity.newIntent(requireContext(), track).apply {
                startActivity(this)
            }
        }

        adapter = TrackAdapter(onTrackClickDebounce)
    }

    private fun renderState(state: FavTracksState) {
        when (state) {
            is FavTracksState.Empty -> showEmpty()
            is FavTracksState.Content -> showContent(state.favTracks)
        }
    }

    private fun showEmpty() {
        binding.favTracksRv.visibility = View.INVISIBLE
        binding.placeholderErrorLayout.root.visibility = View.VISIBLE
        binding.placeholderErrorLayout.placeholderMessage.text =
            getString(R.string.error_empty_library)
    }

    private fun showContent(favTracks: List<Track>) {
        binding.placeholderErrorLayout.root.visibility = View.INVISIBLE
        binding.favTracksRv.visibility = View.VISIBLE
        this.favTracks.clear()
        this.favTracks.addAll(favTracks)
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavouriteTracksFragment()
        private const val CLICK_DEBOUNCE_DELAY_IN_MILLIS = 1000L
    }
}