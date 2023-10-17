package com.example.rickandmortyapiproject.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.RickAndMortyApplication
import com.example.rickandmortyapiproject.adapters.CharactersListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentEpisodeDetailsBinding
import com.example.rickandmortyapiproject.models.Episode
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import com.example.rickandmortyapiproject.viewmodels.EpisodeDetailsViewModel
import com.example.rickandmortyapiproject.viewmodels.EpisodeDetailsViewModelFactory
import kotlinx.coroutines.launch

class EpisodeDetailsFragment : Fragment() {
    private var id: Int = 0
    private var _binding: FragmentEpisodeDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EpisodeDetailsViewModel by activityViewModels {
        EpisodeDetailsViewModelFactory(
            (requireActivity().application as RickAndMortyApplication).database.episodeDao,
            (requireActivity().application as RickAndMortyApplication).database.characterDao
        )
    }
    private val isConnected get() = Utils.isConnectedToNetwork(requireContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { id = it.getInt("id") }
        viewModel.getEpisode(id, isConnected)
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
            val action = EpisodeDetailsFragmentDirections
                .actionEpisodeDetailsFragmentToCharacterDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
        observeEpisode()
        observeCharacters()

        if (!isConnected)
            binding.episodeCharacters.append(getText(R.string.inaccurate_data_no_internet))

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeEpisode() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseEpisodeState.collect { state ->
                    binding.progressCircularMain.isVisible = state is DataState.Loading
                    when (state) {
                        is DataState.Success -> showEpisode(state.data)

                        is DataState.Error -> Utils.onErrorResponse(
                            requireContext(),
                            state.exception
                        )

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
                        is DataState.Success ->
                            (binding.recyclerView.adapter as CharactersListAdapter).data =
                                state.data.toMutableList()

                        is DataState.Error -> Utils.onErrorResponse(
                            requireContext(),
                            state.exception
                        )

                        else -> Unit
                    }
                }
            }
        }
    }
}