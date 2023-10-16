package com.example.rickandmortyapiproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.rickandmortyapiproject.adapters.CharactersListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentRecyclerListBinding
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import com.example.rickandmortyapiproject.viewmodels.CharactersViewModel
import com.example.rickandmortyapiproject.viewmodels.CharactersViewModelFactory
import kotlinx.coroutines.launch

class CharactersFragment : Fragment() {

    private var _binding: FragmentRecyclerListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CharactersViewModel by activityViewModels {
        CharactersViewModelFactory(
            (activity?.application as RickAndMortyApplication).database.characterDao
        )
    }
    private val adapter = CharactersListAdapter()
    private var isLoading: Boolean
        get() = binding.progressCircular.isVisible
        set(value) { binding.progressCircular.isVisible = value }
    private val isLastPage get() = viewModel.nextUrl.isNullOrBlank()
    private var replaceData = false
    private var isObserving = false
    private val isConnected get() = Utils.isConnectedToNetwork(requireContext())


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
        _binding = FragmentRecyclerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.onClick = {
            val action = CharactersFragmentDirections
                .actionNavigationCharactersToCharacterDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        if (!isObserving) observeCharacters()

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            viewModel.get()
            binding.swipeRefresh.isRefreshing = false
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeCharacters() {
        Log.i("CharactersList", "Started an observer")
        isObserving = true
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.responseState.collect { state ->
                    isLoading = state is DataState.Loading
                    when (state) {
                        is DataState.Success -> {
                            Log.i("CharacterList", "observer success")
                            if (replaceData) adapter.data = state.data.toMutableList()
                            else adapter.addData(state.data)

                            replaceData = false
                        }

                        is DataState.Error -> {
                            Log.i("CharacterList", state.exception.toString())
                            if (!isConnected)
                                Utils.showAlertDialog(requireContext(), R.string.no_internet_exception)
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