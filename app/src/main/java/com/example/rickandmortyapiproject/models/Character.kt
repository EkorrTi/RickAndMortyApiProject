package com.example.rickandmortyapiproject.models

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
)

data class CharacterDetails(
    val name: String,
    val url: String
)
