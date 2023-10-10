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

private const val TAG = "EpisodeDetail"

class EpisodeDetailsViewModel : ViewModel() {
    private val _responseEpisodeState = MutableStateFlow<EpisodeDetailState>(EpisodeDetailState.Empty)
    val responseEpisodeState = _responseEpisodeState.asStateFlow()
    private val _responseCharactersState = MutableStateFlow<EpisodeDetailState>(EpisodeDetailState.Empty)
    val responseCharactersState = _responseCharactersState.asStateFlow()

    sealed class EpisodeDetailState{
        object Empty : EpisodeDetailState()
        object Loading : EpisodeDetailState()
        data class Success(val result: Episode) : EpisodeDetailState()
        data class SuccessCharacters(val result: List<Character>) : EpisodeDetailState()
        data class Error(val error: Throwable) : EpisodeDetailState()
    }

    fun getEpisode(id: Int){
        viewModelScope.launch {
            Log.i(TAG, "sending the request")
            _responseEpisodeState.value = EpisodeDetailState.Loading
            try {
                _responseEpisodeState.value = EpisodeDetailState.Success(
                    RNMApi.retrofitService.getEpisode(id)
                )
                Log.i(TAG, responseEpisodeState.value.toString())
                getCharacters(
                    (responseEpisodeState.value as EpisodeDetailState.Success).result.characters
                )
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseEpisodeState.value = EpisodeDetailState.Error(e)
            }
        }
    }

    private fun getCharacters(characters: List<String>){
        val ids = mutableListOf<Int>()
        for (c in characters) ids.add(c.substring(Utils.ID_START_INDEX_CHARACTER).toInt())
        Log.i(TAG, "Extracted episode ids: $ids")

        viewModelScope.launch {
            _responseCharactersState.value = EpisodeDetailState.Loading
            try {
                _responseCharactersState.value = EpisodeDetailState.SuccessCharacters(
                    RNMApi.retrofitService.getCharactersList(ids)
                )
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseCharactersState.value = EpisodeDetailState.Error(e)
            }
        }
    }
}