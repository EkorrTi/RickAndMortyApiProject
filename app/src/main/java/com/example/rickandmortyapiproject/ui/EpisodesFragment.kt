package com.example.rickandmortyapiproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapiproject.adapters.EpisodeListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentRecyclerListBinding
import com.example.rickandmortyapiproject.utils.Utils
import com.example.rickandmortyapiproject.viewmodels.EpisodesViewModel
import kotlinx.coroutines.launch

class EpisodesFragment : Fragment() {

    private var _binding: FragmentRecyclerListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EpisodesViewModel by viewModels()
    private val adapter = EpisodeListAdapter()
    private var isLoading: Boolean
        get() = binding.progressCircular.isVisible
        set(value) {
            binding.progressCircular.isVisible = value
        }
    private val isLastPage get() = viewModel.nextUrl.isNullOrBlank()
    private var replaceData = false
    private var isObserving = false

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
        adapter.onClick = {
            val action = EpisodesFragmentDirections.actionNavigationEpisodesToEpisodeDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        if (!isObserving) observeEpisodes()

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager!! as GridLayoutManager

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        isLoading = true
                        viewModel.getNext()
                    }
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            replaceData = true
            viewModel.get()
            binding.swipeRefresh.isRefreshing = false
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeEpisodes() {
        Log.i("EpisodesList", "started an observer")
        isObserving = true
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.responseState.collect { state ->
                    isLoading = state is EpisodesViewModel.EpisodesState.Loading
                    when (state) {
                        is EpisodesViewModel.EpisodesState.Success -> {
                            Log.i("EpisodesList", "observer success")
                            if (replaceData) adapter.data = state.result.results.toMutableList()
                            else adapter.addData(state.result.results)

                            replaceData = false
                            viewModel.nextUrl = state.result.info.next
                        }

                        is EpisodesViewModel.EpisodesState.Error -> {
                            Utils.onErrorResponse(requireContext(), state.error)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}