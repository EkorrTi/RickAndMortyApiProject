package com.example.rickandmortyapiproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.RickAndMortyApplication
import com.example.rickandmortyapiproject.adapters.LocationListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentLocationsListBinding
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import com.example.rickandmortyapiproject.viewmodels.LocationsViewModel
import com.example.rickandmortyapiproject.viewmodels.LocationsViewModelFactory
import kotlinx.coroutines.launch

class LocationsFragment : Fragment() {

    private var _binding: FragmentLocationsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationsViewModel by activityViewModels {
        LocationsViewModelFactory(
            (requireActivity().application as RickAndMortyApplication).database.locationDao
        )
    }
    private val adapter = LocationListAdapter()
    private var isLoading: Boolean
        get() = binding.recyclerLayout.progressCircular.isVisible
        set(value) {
            binding.recyclerLayout.progressCircular.isVisible = value
        }
    private val isLastPage get() = viewModel.nextUrl.isNullOrBlank()
    private var replaceData = false
    private var isObserving = false
    private val isConnected get() = Utils.isConnectedToNetwork(requireContext())

    val name get() = binding.locationNameEdittext.text.toString()
    val type get() = binding.locationTypeEdittext.text.toString()
    val dimension get() = binding.locationDimensionEdittext.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isConnected) viewModel.get()
        else viewModel.getFromDB()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.onClick = {
            val action = LocationsFragmentDirections
                .actionNavigationLocationsToLocationDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerLayout.recyclerView.adapter = adapter
        if (!isObserving) observeLocations()

        binding.recyclerLayout.recyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager!! as GridLayoutManager

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage && isConnected) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        isLoading = true
                        viewModel.getNext()
                    }
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            replaceData = true
            viewModel.get(name, type, dimension)
            binding.swipeRefresh.isRefreshing = false
        }

        binding.searchButton.setOnClickListener {
            if (!isConnected)
                Toast.makeText(
                    requireContext(),
                    R.string.inaccurate_data_no_internet,
                    Toast.LENGTH_SHORT
                ).show()


            replaceData = true

            if (isConnected)
                viewModel.get(name, type, dimension)
            else
                viewModel.getFromDB(name, type, dimension)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeLocations() {
        Log.i("LocationsList", "started an observer")
        isObserving = true
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseState.collect { state ->
                    isLoading = state is DataState.Loading
                    when (state) {
                        is DataState.Success -> {
                            Log.i("LocationsList", "observer success load: ${state.data}")
                            if (state.data.isEmpty() && !isConnected)
                                binding.recyclerLayout.textAlert.setText(R.string.no_cached_data)
                            else if (state.data.isEmpty())
                                binding.recyclerLayout.textAlert.setText(R.string.no_results)
                            else
                                binding.recyclerLayout.textAlert.text = null

                            if (replaceData) adapter.data = state.data.toMutableList()
                            else adapter.addData(state.data)

                            replaceData = false
                        }

                        is DataState.Error -> {
                            if (!isConnected)
                                Utils.showAlertDialog(
                                    requireContext(),
                                    R.string.no_internet_exception
                                )
                            else
                                Utils.onErrorResponse(requireContext(), state.exception)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}