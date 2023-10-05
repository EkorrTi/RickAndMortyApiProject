package com.example.rickandmortyapiproject.ui.characterDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.models.Episode
import com.example.rickandmortyapiproject.network.RNMApi
import kotlinx.coroutines.launch

private const val ID_START_INDEX = 40

class CharacterDetailsViewModel : ViewModel() {
    private val _responseCharacter = MutableLiveData<Character>()
    val responseCharacter: LiveData<Character> get() = _responseCharacter

    private val _responseEpisodes = MutableLiveData<List<Episode>>()
    val responseEpisodes: LiveData<List<Episode>> get() = _responseEpisodes

    fun getCharacter(id: Int) {
        viewModelScope.launch {
            Log.i("ChD", "sending the request")
            try {
                _responseCharacter.value = RNMApi.retrofitService.getCharacter(id)
            } catch (e: Exception){
                Log.w("ChD", e)
            }
        }
    }

    fun getEpisodes(episodes: List<String>) {
        val ids = mutableListOf<Int>()
        for (e in episodes) ids.add(e.substring(ID_START_INDEX).toInt())
        Log.i("ChD", "Extracted episode ids: $ids")

        viewModelScope.launch {
            try {
                _responseEpisodes.value = RNMApi.retrofitService.getEpisodesList(ids)
            } catch (e: Exception){
                Log.w("ChD", e)
            }
        }
    }
}