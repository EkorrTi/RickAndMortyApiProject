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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.RickAndMortyApplication
import com.example.rickandmortyapiproject.adapters.CharactersListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentLocationDetailsBinding
import com.example.rickandmortyapiproject.models.Location
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import com.example.rickandmortyapiproject.viewmodels.LocationDetailsViewModel
import com.example.rickandmortyapiproject.viewmodels.LocationDetailsViewModelFactory
import kotlinx.coroutines.launch

class LocationDetailsFragment : Fragment() {
    private var id: Int = 0
    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationDetailsViewModel by activityViewModels {
        LocationDetailsViewModelFactory(
            (requireActivity().application as RickAndMortyApplication).database.locationDao,
            (requireActivity().application as RickAndMortyApplication).database.characterDao
        )
    }
    private val isConnected get() = Utils.isConnectedToNetwork(requireContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { id = it.getInt("id") }
        viewModel.getLocation(id, isConnected)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = CharactersListAdapter()
        adapter.onClick = {
            val action = LocationDetailsFragmentDirections
                .actionLocationDetailsFragmentToCharacterDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        observeLocation()
        observeResidents()

        if (!isConnected)
            binding.locationResidents.append(getText(R.string.inaccurate_data_no_internet))

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeLocation() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseLocationState.collect { state ->
                    binding.progressCircularMain.isVisible = state is DataState.Loading
                    when (state) {
                        is DataState.Success -> showLocation(state.data)

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

    private fun showLocation(loc: Location) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = loc.name

        binding.locationName.text = loc.name
        binding.locationType.text = Html.fromHtml(
            getString(R.string.character_type, loc.type),
            Html.FROM_HTML_MODE_LEGACY
        )
        binding.locationDimension.text = Html.fromHtml(
            getString(R.string.location_dimension, loc.dimension),
            Html.FROM_HTML_MODE_LEGACY
        )

        binding.locationResidents.isVisible = true
    }

    private fun observeResidents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseResidentsState.collect { state ->
                    binding.progressCircularSub.isVisible = state is DataState.Loading
                    when (state) {
                        is DataState.Success -> {
                            binding.locationResidentsNone.isVisible = state.data.isEmpty()

                            (binding.recyclerView.adapter as CharactersListAdapter).data =
                                state.data.toMutableList()
                            Log.i("LocationDetailFragment", state.data.toString())
                        }

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