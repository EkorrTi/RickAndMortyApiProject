package com.example.rickandmortyapiproject.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "location")
data class Location(
    @PrimaryKey(autoGenerate = false)
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
