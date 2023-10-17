package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.database.CharacterDao
import com.example.rickandmortyapiproject.database.EpisodeDao
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.models.Episode
import com.example.rickandmortyapiproject.network.NetworkService
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "EpisodeDetail"

class EpisodeDetailsViewModel(
    private val episodeDao: EpisodeDao,
    private val characterDao: CharacterDao
) : ViewModel() {
    private val _responseEpisodeState = MutableStateFlow<DataState<Episode>>(DataState.Empty)
    val responseEpisodeState = _responseEpisodeState.asStateFlow()
    private val _responseCharactersState = MutableStateFlow<DataState<List<Character>>>(DataState.Empty)
    val responseCharactersState = _responseCharactersState.asStateFlow()

    fun getEpisode(id: Int, isConnected: Boolean){
        viewModelScope.launch {
            Log.i(TAG, "sending the request")
            _responseEpisodeState.value = DataState.Loading
            try {
                val ep = if (isConnected)
                    NetworkService.retrofitService.getEpisode(id)
                else
                    episodeDao.getById(id)
                _responseEpisodeState.value = DataState.Success(ep)

                Log.i(TAG, responseEpisodeState.value.toString())
                getCharacters(ep.characters, isConnected)
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseEpisodeState.value = DataState.Error(e)
            }
        }
    }

    private fun getCharacters(characters: List<String>, isConnected: Boolean){
        val ids = Utils.extractCharacterIds(characters)
        Log.i(TAG, "Extracted character ids: $ids")

        viewModelScope.launch {
            _responseCharactersState.value = DataState.Loading
            try {
                if (isConnected) {
                    val response = NetworkService.retrofitService.getCharactersByIds(ids)
                    _responseCharactersState.value = DataState.Success(response)
                    characterDao.insert(response)
                } else
                    _responseCharactersState.value = DataState.Success(
                        characterDao.getAllById(ids)
                    )
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseCharactersState.value = DataState.Error(e)
            }
        }
    }
}

class EpisodeDetailsViewModelFactory(
    private val episodeDao: EpisodeDao,
    private val characterDao: CharacterDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodeDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EpisodeDetailsViewModel(episodeDao, characterDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}