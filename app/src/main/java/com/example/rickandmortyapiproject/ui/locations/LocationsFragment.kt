package com.example.rickandmortyapiproject.ui.locations

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
import com.example.rickandmortyapiproject.adapters.LocationListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentRecyclerListBinding
import com.example.rickandmortyapiproject.ui.utils.Utils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationsFragment : Fragment() {

    private var _binding: FragmentRecyclerListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationsViewModel by viewModels()

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
        val adapter = LocationListAdapter()
        adapter.onClick = {
            val action = LocationsFragmentDirections
                .actionNavigationLocationsToLocationDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
        observeLocations()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.get()
            observeLocations()
            binding.swipeRefresh.isRefreshing = false
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeLocations() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseState.collect { state ->
                    binding.progressCircular.isGone =
                        state !is LocationsViewModel.LocationsState.Loading
                    when (state) {
                        is LocationsViewModel.LocationsState.Success -> {
                            (binding.recyclerView.adapter as LocationListAdapter)
                                .data = state.result.results
                            cancel("Successful")
                        }

                        is LocationsViewModel.LocationsState.Error -> {
                            Utils.onErrorResponse(requireContext(), state.error)
                            cancel("Error")
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