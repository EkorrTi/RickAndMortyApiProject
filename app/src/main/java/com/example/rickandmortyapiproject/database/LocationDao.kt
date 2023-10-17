package com.example.rickandmortyapiproject.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortyapiproject.models.Location

@Dao
interface LocationDao {
    @Query("SELECT * FROM location WHERE id = :id")
    suspend fun getById(id: Int): Location

    @Query(
        """SELECT * FROM location
        WHERE (:name IS NULL OR name LIKE :name)
        AND (:type IS NULL OR type LIKE :type)
        AND (:dimension IS NULL OR dimension LIKE :dimension)"""
    )
    suspend fun getAll(
        name: String? = null,
        type: String? = null,
        dimension: String? = null
    ): List<Location>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locations: List<Location>)
}