package com.example.rickandmortyapiproject.database

import androidx.room.TypeConverter
import com.example.rickandmortyapiproject.models.CharacterDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringtoStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String = Gson().toJson(list)

    @TypeConverter
    fun fromStringtoCharacterDetails(value: String): CharacterDetails = Gson().fromJson(value, CharacterDetails::class.java)

    @TypeConverter
    fun fromCharacterDetails(details: CharacterDetails): String = Gson().toJson(details)
}