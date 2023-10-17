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

private const val TAG = "CharacterDetail"

class CharacterDetailsViewModel(
    private val characterDao: CharacterDao,
    private val episodeDao: EpisodeDao
) : ViewModel() {
    private val _responseCharacterState = MutableStateFlow<DataState<Character>>(DataState.Empty)
    val responseCharacterState = _responseCharacterState.asStateFlow()

    private val _responseEpisodesState = MutableStateFlow<DataState<List<Episode>>>(DataState.Empty)
    val responseEpisodesState = _responseEpisodesState.asStateFlow()

    fun getCharacter(id: Int, isConnected: Boolean) {
        viewModelScope.launch {
            Log.i(TAG, "sending the request")
            _responseCharacterState.value = DataState.Loading
            try {
                val char = if (isConnected)
                    NetworkService.retrofitService.getCharacter(id)
                else
                    characterDao.getById(id)
                _responseCharacterState.value = DataState.Success(char)
                getAppearances(char.episode, isConnected)

                Log.i(TAG, responseCharacterState.value.toString())
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseCharacterState.value = DataState.Error(e)
            }
        }
    }

    private fun getAppearances(episodes: List<String>, isConnected: Boolean) {
        val ids = Utils.extractEpisodeIds(episodes)
        Log.i(TAG, "Extracted episode ids: $ids")

        viewModelScope.launch {
            _responseEpisodesState.value = DataState.Loading
            try {
                if (isConnected) {
                    val response = NetworkService.retrofitService.getEpisodesByIds(ids)
                    _responseEpisodesState.value = DataState.Success(response)
                    episodeDao.insert(response)
                } else
                    _responseEpisodesState.value = DataState.Success(episodeDao.getAllById(ids))

                Log.i(TAG, responseEpisodesState.value.toString())
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseEpisodesState.value = DataState.Error(e)
            }
        }
    }
}

class CharacterDetailsViewModelFactory(
    private val characterDao: CharacterDao,
    private val episodeDao: EpisodeDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharacterDetailsViewModel(characterDao, episodeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}