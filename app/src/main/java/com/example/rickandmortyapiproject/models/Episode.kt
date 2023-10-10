package com.example.rickandmortyapiproject.models

import java.io.Serializable

data class Episode(
    val id: Int,
    val name: String,
    val airDate: String,
    val episode: String,
    val characters: List<String>,
): Serializable {
    override fun toString(): String {
        return "Episode(id=$id, name='$name', airDate='$airDate', episode='$episode', characters=${characters.size})"
    }
}
