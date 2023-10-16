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

private const val TAG = "CharacterDetail"

class CharacterDetailsViewModel : ViewModel() {
    private val _responseCharacterState = MutableStateFlow<DataState<Character>>(DataState.Empty)
    val responseCharacterState = _responseCharacterState.asStateFlow()

    private val _responseEpisodesState = MutableStateFlow<DataState<List<Episode>>>(DataState.Empty)
    val responseEpisodesState = _responseEpisodesState.asStateFlow()

    fun getCharacter(id: Int) {
        viewModelScope.launch {
            Log.i(TAG, "sending the request")
            _responseCharacterState.value = DataState.Loading
            try {
                val char = NetworkService.retrofitService.getCharacter(id)
                _responseCharacterState.value = DataState.Success(char)
                Log.i(TAG, responseCharacterState.value.toString())
                getAppearances(char.episode)
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseCharacterState.value = DataState.Error(e)
            }
        }
    }

    private fun getAppearances(episodes: List<String>) {
        val ids = Utils.extractEpisodeIds(episodes)
        Log.i(TAG, "Extracted episode ids: $ids")

        viewModelScope.launch {
            _responseEpisodesState.value = DataState.Loading
            try {
                _responseEpisodesState.value = DataState.Success(
                    NetworkService.retrofitService.getEpisodesList(ids)
                )
                Log.i(TAG, responseEpisodesState.value.toString())
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseEpisodesState.value = DataState.Error(e)
            }
        }
    }
}