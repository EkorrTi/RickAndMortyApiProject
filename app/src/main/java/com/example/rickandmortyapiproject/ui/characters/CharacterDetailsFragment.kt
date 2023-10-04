package com.example.rickandmortyapiproject.ui.characters

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.example.rickandmortyapiproject.R
import com.example.rickandmortyapiproject.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmortyapiproject.models.Character

class CharacterDetailsFragment : Fragment() {
    private var char: Character? = null
    private var _binding: FragmentCharacterDetailsBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            char = it.getSerializable("character") as Character
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        char?.let {
            binding.characterName.text = it.name
            binding.characterImage.load(it.image)
            binding.characterStatus.text = Html.fromHtml(
                resources.getString(R.string.character_status, it.status),
                Html.FROM_HTML_MODE_LEGACY
            )
            binding.characterSpecies.text = Html.fromHtml(
                resources.getString(R.string.character_species, it.species),
                Html.FROM_HTML_MODE_LEGACY
            )
            binding.characterType.text =
                if (it.type.isBlank()) resources.getText(R.string.character_type_standard)
                else Html.fromHtml(
                    resources.getString(R.string.character_type, it.type),
                    Html.FROM_HTML_MODE_LEGACY
                )
            binding.characterGender.text = Html.fromHtml(
                resources.getString(R.string.character_gender, it.gender),
                Html.FROM_HTML_MODE_LEGACY
            )
            binding.characterOrigin.text = Html.fromHtml(
                resources.getString(R.string.character_origin, it.origin.name),
                Html.FROM_HTML_MODE_LEGACY
            )
            binding.characterLocation.text = Html.fromHtml(
                resources.getString(R.string.character_location, it.location.name),
                Html.FROM_HTML_MODE_LEGACY
            )
        }

        super.onViewCreated(view, savedInstanceState)
    }
}