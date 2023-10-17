package com.example.rickandmortyapiproject.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.adapters.EpisodeListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import com.example.rickandmortyapiproject.viewmodels.CharacterDetailsViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CharacterDetailsFragment : Fragment() {
    private var id: Int = 0
    private var _binding: FragmentCharacterDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CharacterDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { id = it.getInt("id") }
        viewModel.getCharacter(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = EpisodeListAdapter()
        adapter.onClick = {
            val action = CharacterDetailsFragmentDirections.actionCharacterDetailsFragmentToEpisodeDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
        observeCharacter()
        observeAppearances()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeCharacter(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.responseCharacterState.collect{ state ->
                    binding.progressCircularMain.isVisible = state is DataState.Loading
                    when (state) {
                        is DataState.Success -> {
                            showCharacter(state.data)
                            cancel("Successful")
                        }

                        is DataState.Error -> {
                            Utils.onErrorResponse(requireContext(), state.exception)
                            cancel("Error", state.exception)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showCharacter(character: Character){
        (requireActivity() as AppCompatActivity).supportActionBar?.title = character.name

        binding.characterName.text = character.name
        binding.characterImage.load(character.image)
        binding.characterStatus.text = Html.fromHtml(
            resources.getString(R.string.character_status, character.status),
            Html.FROM_HTML_MODE_LEGACY
        )
        binding.characterSpecies.text = Html.fromHtml(
            resources.getString(R.string.character_species, character.species),
            Html.FROM_HTML_MODE_LEGACY
        )
        binding.characterType.text =
            if (character.type.isBlank()) resources.getText(R.string.character_type_standard)
            else Html.fromHtml(
                resources.getString(R.string.type, character.type),
                Html.FROM_HTML_MODE_LEGACY
            )
        binding.characterGender.text = Html.fromHtml(
            resources.getString(R.string.character_gender, character.gender),
            Html.FROM_HTML_MODE_LEGACY
        )
        binding.characterOrigin.text = Html.fromHtml(
            resources.getString(R.string.character_origin, character.origin.name),
            Html.FROM_HTML_MODE_LEGACY
        )
        if (character.origin.name != "unknown") {
            binding.characterOrigin.setOnClickListener {
                val action = CharacterDetailsFragmentDirections
                    .actionCharacterDetailsFragmentToLocationDetailsFragment(
                        Utils.extractCharacterLocationId(character.origin.url)
                    )
                findNavController().navigate(action)
            }
        }
        binding.characterLocation.text = Html.fromHtml(
            resources.getString(R.string.character_location, character.location.name),
            Html.FROM_HTML_MODE_LEGACY
        )
        if (character.location.name != "unknown"){
            binding.characterLocation.setOnClickListener {
                val action = CharacterDetailsFragmentDirections
                    .actionCharacterDetailsFragmentToLocationDetailsFragment(
                        Utils.extractCharacterLocationId(character.location.url)
                    )
                findNavController().navigate(action)
            }
        }

        binding.characterAppearances.isVisible = true
    }

    private fun observeAppearances(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.responseEpisodesState.collect{ state ->
                    binding.progressCircularSub.isVisible = state is DataState.Loading
                    when (state) {
                        is DataState.Success -> {
                            (binding.recyclerView.adapter as EpisodeListAdapter).data = state.data.toMutableList()
                            cancel("Success")
                        }
                        is DataState.Error -> {
                            Utils.onErrorResponse(requireContext(), state.exception)
                            cancel("Error", state.exception)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}