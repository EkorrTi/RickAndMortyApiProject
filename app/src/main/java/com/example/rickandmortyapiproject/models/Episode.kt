package com.example.rickandmortyapiproject.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "episode")
data class Episode(
    @PrimaryKey(autoGenerate = false)
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
