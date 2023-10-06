package com.example.rickandmortyapiproject.ui.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.rickandmortyapiproject.adapters.EpisodeListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentRecyclerListBinding
import com.example.rickandmortyapiproject.ui.utils.Utils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EpisodesFragment : Fragment() {

    private var _binding: FragmentRecyclerListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EpisodesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = EpisodeListAdapter()
        adapter.onClick = {
            val action = EpisodesFragmentDirections.actionNavigationEpisodesToEpisodeDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
        observeEpisodes()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.get()
            observeEpisodes()
            binding.swipeRefresh.isRefreshing = false
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeEpisodes() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseState.collect { state ->
                    binding.progressCircular.isGone =
                        state !is EpisodesViewModel.EpisodesState.Loading
                    when (state) {
                        is EpisodesViewModel.EpisodesState.Success -> {
                            (binding.recyclerView.adapter as EpisodeListAdapter)
                                .data = state.result.results
                            cancel("Successful")
                        }

                        is EpisodesViewModel.EpisodesState.Error -> {
                            Utils.onErrorResponse(requireContext(), state.error)
                            cancel("Error", state.error)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}