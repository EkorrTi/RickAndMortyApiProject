package com.example.rickandmortyapiproject.ui.characterDetails

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.adapters.EpisodeListAdapter
import com.example.rickandmortyapiproject.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.ui.utils.Utils
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
        binding.recyclerView.adapter = adapter
        observeCharacter()
        observeAppearances()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeCharacter(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.responseCharacterState.collect{ state ->
                    binding.progressCircularMain.isGone = state !is CharacterDetailsViewModel.CharacterDetailState.Loading
                    when (state) {
                        is CharacterDetailsViewModel.CharacterDetailState.Success -> {
                            showCharacter(state.result)
                            cancel("Successful")
                        }

                        is CharacterDetailsViewModel.CharacterDetailState.Error -> {
                            Utils.onErrorResponse(requireContext(), state.error)
                            cancel("Error", state.error)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showCharacter(character: Character){
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
        binding.characterLocation.text = Html.fromHtml(
            resources.getString(R.string.character_location, character.location.name),
            Html.FROM_HTML_MODE_LEGACY
        )
    }

    private fun observeAppearances(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.responseEpisodesState.collect{ state ->
                    binding.progressCircularSub.isGone = state !is CharacterDetailsViewModel.CharacterDetailState.Loading
                    when (state) {
                        is CharacterDetailsViewModel.CharacterDetailState.SuccessAppearances -> {
                            (binding.recyclerView.adapter as EpisodeListAdapter)
                                .data = state.result
                            cancel("Success")
                        }
                        is CharacterDetailsViewModel.CharacterDetailState.Error -> {
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