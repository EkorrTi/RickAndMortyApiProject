package com.example.rickandmortyapiproject.ui

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.adapters.CharactersListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentEpisodeDetailsBinding
import com.example.rickandmortyapiproject.models.Episode
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import com.example.rickandmortyapiproject.viewmodels.EpisodeDetailsViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EpisodeDetailsFragment : Fragment() {
    private var id: Int = 0
    private var _binding: FragmentEpisodeDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EpisodeDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { id = it.getInt("id") }
        viewModel.getEpisode(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = CharactersListAdapter()
        adapter.onClick = {
            val action = EpisodeDetailsFragmentDirections.actionEpisodeDetailsFragmentToCharacterDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
        observeEpisode()
        observeCharacters()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeEpisode() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseEpisodeState.collect { state ->
                    binding.progressCircularMain.isVisible = state is DataState.Loading
                    when (state) {
                        is DataState.Success -> {
                            showEpisode(state.data)
                            cancel("Successful")
                        }

                        is DataState.Error -> {
                            Utils.onErrorResponse(requireContext(), state.exception)
                            cancel("Error", state.exception)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showEpisode(ep: Episode) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ep.name

        binding.episodeName.text = ep.name
        binding.episodeDate.text = Html.fromHtml(
            getString(R.string.episode_air_date, ep.airDate),
            Html.FROM_HTML_MODE_LEGACY
        )
        binding.episodeCode.text = Html.fromHtml(
            getString(R.string.episode_code, ep.episode),
            Html.FROM_HTML_MODE_LEGACY
        )

        binding.episodeCharacters.isVisible = true
    }

    private fun observeCharacters() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseCharactersState.collect { state ->
                    binding.progressCircularSub.isVisible = state is DataState.Loading
                    when (state) {
                        is DataState.Success -> {
                            Log.i("EpisodeDetails", "observer success")
                            (binding.recyclerView.adapter as CharactersListAdapter).data = state.data.toMutableList()
                            cancel("Successful")
                        }

                        is DataState.Error -> {
                            Utils.onErrorResponse(requireContext(), state.exception)
                            cancel("Error", state.exception)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}