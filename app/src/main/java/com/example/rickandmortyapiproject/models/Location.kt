package com.example.rickandmortyapiproject.models

import java.io.Serializable

data class Location(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>,
): Serializable {
    override fun toString(): String {
        return "Location(id=$id, name='$name', type='$type', dimension='$dimension', residents=${residents.size})"
    }
}
