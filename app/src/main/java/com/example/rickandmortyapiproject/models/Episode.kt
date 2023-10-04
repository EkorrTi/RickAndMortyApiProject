package com.example.rickandmortyapiproject.models

import java.io.Serializable

data class Episode(
    val id: Int,
    val name: String,
    val airDate: String,
    val episode: String,
    val characters: List<String>,
): Serializable
