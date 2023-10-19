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
        WHERE (name LIKE :name)
        AND (type LIKE :type)
        AND (dimension LIKE :dimension)"""
    )
    suspend fun getAll(
        name: String,
        type: String,
        dimension: String
    ): List<Location>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locations: List<Location>)
}