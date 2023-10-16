package com.example.rickandmortyapiproject.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortyapiproject.models.Character

@Dao
interface CharacterDao {
    @Query("SELECT * FROM character WHERE id = :id")
    fun getById(id: Int): Character

    @Query(
        """SELECT * FROM character
        WHERE (:name IS NULL OR name LIKE :name)
        AND (:status IS NULL OR status LIKE :status)
        AND (:species IS NULL OR species LIKE :species)
        AND (:type IS NULL OR type LIKE :type)
        AND (:gender IS NULL OR gender LIKE :gender)
    """)
    fun getAll(
        name: String? = null,
        status: String? = null,
        species: String? = null,
        type: String? = null,
        gender: String? = null,
    ): List<Character>

    @Query("SELECT * FROM character WHERE id IN (:ids)")
    fun getAllById(ids: List<Int>): List<Character>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(char: Character)
}