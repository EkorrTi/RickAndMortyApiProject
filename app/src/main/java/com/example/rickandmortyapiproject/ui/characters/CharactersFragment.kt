package com.example.rickandmortyapiproject.ui.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.rickandmortyapiproject.adapters.CharactersListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentRecyclerListBinding
import com.example.rickandmortyapiproject.models.CharactersApiResponse
import com.example.rickandmortyapiproject.ui.characters.CharactersViewModel.CharactersState
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
        // adapter.onClick = {}
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseState.collect { state ->
                    binding.progressCircular.isGone = state !is CharactersState.Loading
                    when (state) {
                        is CharactersState.Success -> {
                            onSuccessfulResponse(state.result)
                            cancel("Successful")
                        }

                        is CharactersState.Error -> {
                            onErrorResponse(state.error)
                            cancel("Error")
                        }

                        else -> Unit
                    }
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun onSuccessfulResponse(response: CharactersApiResponse) {
        (binding.recyclerView.adapter as CharactersListAdapter).apply {
            data = response.results
            notifyDataSetChanged()
        }
    }

    private fun onErrorResponse(e: Throwable) {
        Toast.makeText(requireContext(), "Error $e", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}