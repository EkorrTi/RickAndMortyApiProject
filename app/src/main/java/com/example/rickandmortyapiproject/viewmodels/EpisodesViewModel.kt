package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.database.EpisodeDao
import com.example.rickandmortyapiproject.models.Episode
import com.example.rickandmortyapiproject.network.NetworkService
import com.example.rickandmortyapiproject.util.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "EpisodesListViewModel"

class EpisodesViewModel(private val episodeDao: EpisodeDao): ViewModel() {
    private val _responseState = MutableStateFlow<DataState<List<Episode>>>(DataState.Empty)
    val responseState = _responseState.asStateFlow()
    var nextUrl: String? = null

    fun getFromDB(
        name: String? = null,
        episode: String? = null
    ) {
        viewModelScope.launch {
            Log.i(TAG, "Fetching from database")
            _responseState.value = DataState.Loading
            _responseState.value = DataState.Success(
                episodeDao.getAll(name, episode)
            )
        }
    }

    private suspend fun insertIntoDD(episodes: List<Episode>) = episodeDao.insert(episodes)

    fun get(
        name: String? = null,
        episode: String? = null
    ){
        viewModelScope.launch {
            Log.i(TAG, "Fetching characters")
            _responseState.value = DataState.Loading
            try {
                val response = NetworkService.retrofitService.getEpisodes(name, episode)
                nextUrl = response.info.next
                _responseState.value = DataState.Success(response.results)
                insertIntoDD(response.results)
                Log.i(TAG, responseState.value.toString())
            } catch (e: Exception){
                Log.w(TAG, e)
                _responseState.value = DataState.Error(e)
            }
        }
    }

    fun getNext(){
        viewModelScope.launch {
            Log.i(TAG, "fetching next")
            _responseState.value = DataState.Loading
            try {
                val response = NetworkService.retrofitService.getEpisodes(nextUrl!!)
                nextUrl = response.info.next
                _responseState.value = DataState.Success(response.results)
                insertIntoDD(response.results)
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseState.value = DataState.Error(e)
            }
        }
    }
}

class EpisodesViewModelFactory(private val episodeDao: EpisodeDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EpisodesViewModel(episodeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}