package com.example.rickandmortyapiproject.models

import java.io.Serializable

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: CharacterDetails,
    val location: CharacterDetails,
    val image: String,
    val episode: List<String>,
): Serializable

data class CharacterDetails(
    val name: String,
    val url: String
)
