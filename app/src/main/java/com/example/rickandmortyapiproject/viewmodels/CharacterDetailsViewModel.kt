package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.models.Episode
import com.example.rickandmortyapiproject.network.RNMApi
import com.example.rickandmortyapiproject.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CharacterDetail"

class CharacterDetailsViewModel : ViewModel() {
    private val _responseCharacterState =
        MutableStateFlow<CharacterDetailState>(CharacterDetailState.Empty)
    val responseCharacterState = _responseCharacterState.asStateFlow()

    sealed class CharacterDetailState {
        object Empty : CharacterDetailState()
        object Loading : CharacterDetailState()
        data class Success(val result: Character) : CharacterDetailState()
        data class SuccessAppearances(val result: List<Episode>) : CharacterDetailState()
        data class Error(val error: Throwable) : CharacterDetailState()
    }

    private val _responseEpisodesState =
        MutableStateFlow<CharacterDetailState>(CharacterDetailState.Empty)
    val responseEpisodesState = _responseEpisodesState.asStateFlow()

    fun getCharacter(id: Int) {
        viewModelScope.launch {
            Log.i(TAG, "sending the request")
            _responseCharacterState.value = CharacterDetailState.Loading
            try {
                _responseCharacterState.value = CharacterDetailState.Success(
                    RNMApi.retrofitService.getCharacter(id)
                )
                Log.i(TAG, responseCharacterState.value.toString())
                getAppearances(
                    (_responseCharacterState.value as CharacterDetailState.Success)
                        .result.episode
                )
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseCharacterState.value = CharacterDetailState.Error(e)
            }
        }
    }

    private fun getAppearances(episodes: List<String>) {
        val ids = mutableListOf<Int>()
        for (e in episodes) ids.add(e.substring(Utils.ID_START_INDEX_EPISODE).toInt())
        Log.i(TAG, "Extracted episode ids: $ids")

        viewModelScope.launch {
            _responseEpisodesState.value = CharacterDetailState.Loading
            try {
                _responseEpisodesState.value = CharacterDetailState.SuccessAppearances(
                    RNMApi.retrofitService.getEpisodesList(ids)
                )
                Log.i(TAG, responseEpisodesState.value.toString())
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseEpisodesState.value = CharacterDetailState.Error(e)
            }
        }
    }
}