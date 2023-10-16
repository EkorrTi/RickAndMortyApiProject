package com.example.rickandmortyapiproject.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.models.Episode
import com.example.rickandmortyapiproject.models.Location

@Database(
    entities = [Character::class, Location::class, Episode::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val episodeDao: EpisodeDao
    abstract val locationDao: LocationDao
    abstract val characterDao: CharacterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "RickAndMortyDatabase"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}