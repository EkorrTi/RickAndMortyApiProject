package com.example.rickandmortyapiproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.example.rickandmortyapiproject.adapters.CharactersListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentCharactersListBinding
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import com.example.rickandmortyapiproject.viewmodels.CharactersViewModel
import com.example.rickandmortyapiproject.viewmodels.CharactersViewModelFactory
import kotlinx.coroutines.launch

class CharactersFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentCharactersListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CharactersViewModel by activityViewModels {
        CharactersViewModelFactory(
            (requireActivity().application as RickAndMortyApplication).database.characterDao
        )
    }
    private val adapter = CharactersListAdapter()
    private var isLoading: Boolean
        get() = binding.recyclerLayout.progressCircular.isVisible
        set(value) {
            binding.recyclerLayout.progressCircular.isVisible = value
        }
    private val isLastPage get() = viewModel.nextUrl.isNullOrBlank()
    private var replaceData = false
    private var isObserving = false
    private val isConnected get() = Utils.isConnectedToNetwork(requireContext())

    val name get() = binding.characterNameEdittext.text.toString()
    val species get() = binding.characterSpeciesEdittext.text.toString()
    val type get() = binding.characterTypeEdittext.text.toString()
    private var status = ""
    private var gender = ""


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
        _binding = FragmentCharactersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.onClick = {
            val action = CharactersFragmentDirections
                .actionNavigationCharactersToCharacterDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerLayout.recyclerView.adapter = adapter

        if (!isObserving) observeCharacters()

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
            viewModel.get(name, status, species, type, gender)
            binding.swipeRefresh.isRefreshing = false
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerStatus.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGender.adapter = adapter
        }

        binding.spinnerStatus.onItemSelectedListener = this
        binding.spinnerGender.onItemSelectedListener = this

        binding.searchButton.setOnClickListener {
            if (!isConnected)
                Toast.makeText(
                    requireContext(),
                    R.string.inaccurate_data_no_internet,
                    Toast.LENGTH_SHORT
                ).show()
            replaceData = true

            if (isConnected)
                viewModel.get(name, status, species, type, gender)
            else
                viewModel.getFromDB(name, status, species, type, gender)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val item = if (pos == 0)
            ""
        else
            parent?.getItemAtPosition(pos).toString()

        if (parent?.id == binding.spinnerStatus.id)
            status = item
        else
            gender = item
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun observeCharacters() {
        Log.i("CharactersList", "Started an observer")
        isObserving = true
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseState.collect { state ->
                    isLoading = state is DataState.Loading
                    when (state) {
                        is DataState.Success -> {
                            Log.i("CharacterList", "observer success load: ${state.data}")
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
                            Log.w("CharacterList", state.exception.toString())

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