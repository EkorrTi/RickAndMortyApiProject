package com.example.rickandmortyapiproject.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortyapiproject.models.Character

@Dao
interface CharacterDao {
    @Query("SELECT * FROM character WHERE id = :id")
    suspend fun getById(id: Int): Character

    @Query(
        """SELECT * FROM character
        WHERE (name LIKE :name)
        AND (status LIKE :status)
        AND (species LIKE :species)
        AND (type LIKE :type)
        AND (gender LIKE :gender)
    """)
    suspend fun getAll(
        name: String,
        status: String,
        species: String,
        type: String,
        gender: String,
    ): List<Character>

    @Query("SELECT * FROM character WHERE id IN (:ids)")
    suspend fun getAllById(ids: List<Int>): List<Character>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(characters: List<Character>)
}