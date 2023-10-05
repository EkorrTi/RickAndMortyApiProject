package com.example.rickandmortyapiproject.ui.episodes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.models.EpisodesApiResponse
import com.example.rickandmortyapiproject.network.RNMApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "EpisodesList"

class EpisodesViewModel: ViewModel() {
    private val _responseState = MutableStateFlow<EpisodesState>(EpisodesState.Empty)
    val responseState = _responseState.asStateFlow()

    sealed class EpisodesState {
        object Empty: EpisodesState()
        object Loading: EpisodesState()
        data class Success(val result: EpisodesApiResponse): EpisodesState()
        data class Error(val error: Throwable): EpisodesState()
    }

    fun get(
        page: Int? = null,
        name: String? = null,
        episode: String? = null
    ){
        viewModelScope.launch {
            Log.i(TAG, "Fetching characters")
            _responseState.value = EpisodesState.Loading
            try {
                _responseState.value = EpisodesState.Success(
                    RNMApi.retrofitService.getEpisodes(page, name, episode)
                )
                Log.i(TAG, responseState.value.toString())
            } catch (e: Exception){
                Log.w(TAG, e)
                _responseState.value = EpisodesState.Error(e)
            }
        }
    }
}