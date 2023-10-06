package com.example.rickandmortyapiproject.ui.locationDetails

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.adapters.CharactersListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentLocationDetailsBinding
import com.example.rickandmortyapiproject.models.Location
import com.example.rickandmortyapiproject.ui.utils.Utils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationDetailsFragment : Fragment() {
    private var id: Int = 0
    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { id = it.getInt("id") }
        viewModel.getLocation(id)
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

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeLocation(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.responseLocationState.collect{ state ->
                    binding.progressCircularMain.isGone = state !is LocationDetailsViewModel.LocationDetailState.Loading
                    when (state) {
                        is LocationDetailsViewModel.LocationDetailState.Success -> {
                            showLocation(state.result)
                            cancel("Successful")
                        }

                        is LocationDetailsViewModel.LocationDetailState.Error -> {
                            Utils.onErrorResponse(requireContext(), state.error)
                            cancel("Error", state.error)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showLocation(loc: Location){
        binding.locationName.text = loc.name
        binding.locationType.text = Html.fromHtml(
            getString(R.string.type, loc.type),
            Html.FROM_HTML_MODE_LEGACY
        )
        binding.locationDimension.text = Html.fromHtml(
            getString(R.string.location_dimension, loc.dimension),
            Html.FROM_HTML_MODE_LEGACY
        )
    }

    private fun observeResidents(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.responseResidentsState.collect{ state ->
                    binding.progressCircularSub.isGone = state !is LocationDetailsViewModel.LocationDetailState.Loading
                    when (state) {
                        is LocationDetailsViewModel.LocationDetailState.SuccessResidents -> {
                            (binding.recyclerView.adapter as CharactersListAdapter).data = state.result
                            cancel("Success")
                        }

                        is LocationDetailsViewModel.LocationDetailState.NoResidents -> {
                            binding.locationResidentsNone.isVisible = true
                            cancel("Success")
                        }

                        is LocationDetailsViewModel.LocationDetailState.Error -> {
                            Utils.onErrorResponse(requireContext(), state.error)
                            cancel("Error", state.error)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}