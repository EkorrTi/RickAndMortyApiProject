package com.example.rickandmortyapiproject.ui.characters

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
import com.example.rickandmortyapiproject.adapters.CharactersListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentRecyclerListBinding
import com.example.rickandmortyapiproject.ui.utils.Utils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CharactersFragment : Fragment() {

    private var _binding: FragmentRecyclerListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CharactersViewModel by viewModels()

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
        val adapter = CharactersListAdapter()
        adapter.onClick = {
            val action = CharactersFragmentDirections
                .actionNavigationCharactersToCharacterDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
        observeCharacters()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.get()
            observeCharacters()
            binding.swipeRefresh.isRefreshing = false
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeCharacters() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseState.collect { state ->
                    binding.progressCircular.isGone =
                        state !is CharactersViewModel.CharactersState.Loading
                    when (state) {
                        is CharactersViewModel.CharactersState.Success -> {
                            (binding.recyclerView.adapter as CharactersListAdapter)
                                .data = state.result.results
                            cancel("Successful")
                        }

                        is CharactersViewModel.CharactersState.Error -> {
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