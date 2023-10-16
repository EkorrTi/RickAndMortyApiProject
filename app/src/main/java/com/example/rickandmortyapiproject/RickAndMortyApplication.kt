package com.example.rickandmortyapiproject

import android.app.Application
import com.example.rickandmortyapiproject.database.AppDatabase

class RickAndMortyApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}