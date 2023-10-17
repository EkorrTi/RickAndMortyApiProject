package com.example.rickandmortyapiproject.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortyapiproject.models.Episode

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episode WHERE id = :id")
    suspend fun getById(id: Int): Episode

    @Query(
        """SELECT * FROM episode 
        WHERE (:name IS NULL OR name LIKE :name)
        AND (:episode IS NULL OR episode LIKE :episode)"""
    )
    suspend fun getAll(
        name: String? = null,
        episode: String? = null
    ): List<Episode>

    @Query("SELECT * FROM episode WHERE id IN (:ids)")
    suspend fun getAllById(ids: List<Int>): List<Episode>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(episodes: List<Episode>)
}