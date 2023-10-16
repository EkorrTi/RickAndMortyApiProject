package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.models.Episode
import com.example.rickandmortyapiproject.network.NetworkService
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "EpisodeDetail"

class EpisodeDetailsViewModel : ViewModel() {
    private val _responseEpisodeState = MutableStateFlow<DataState<Episode>>(DataState.Empty)
    val responseEpisodeState = _responseEpisodeState.asStateFlow()
    private val _responseCharactersState = MutableStateFlow<DataState<List<Character>>>(DataState.Empty)
    val responseCharactersState = _responseCharactersState.asStateFlow()

    fun getEpisode(id: Int){
        viewModelScope.launch {
            Log.i(TAG, "sending the request")
            _responseEpisodeState.value = DataState.Loading
            try {
                val ep = NetworkService.retrofitService.getEpisode(id)
                _responseEpisodeState.value = DataState.Success(ep)
                Log.i(TAG, responseEpisodeState.value.toString())
                getCharacters(ep.characters)
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseEpisodeState.value = DataState.Error(e)
            }
        }
    }

    private fun getCharacters(characters: List<String>){
        val ids = Utils.extractCharacterIds(characters)
        Log.i(TAG, "Extracted episode ids: $ids")

        viewModelScope.launch {
            _responseCharactersState.value = DataState.Loading
            try {
                _responseCharactersState.value = DataState.Success(
                    NetworkService.retrofitService.getCharactersList(ids)
                )
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseCharactersState.value = DataState.Error(e)
            }
        }
    }
}