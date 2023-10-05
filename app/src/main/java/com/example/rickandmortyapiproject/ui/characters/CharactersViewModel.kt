package com.example.rickandmortyapiproject.ui.characters

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.models.CharactersApiResponse
import com.example.rickandmortyapiproject.network.RNMApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CharactersList"

class CharactersViewModel: ViewModel() {
    private val _responseState = MutableStateFlow<CharactersState>(CharactersState.Empty)
    val responseState = _responseState.asStateFlow()

    sealed class CharactersState {
        object Empty: CharactersState()
        object Loading: CharactersState()
        data class Success(val result: CharactersApiResponse): CharactersState()
        data class Error(val error: Throwable): CharactersState()
    }

    fun get(
        page: Int? = null,
        name: String? = null,
        status: String? = null,
        species: String? = null,
        type: String? = null,
        gender: String? = null,
    ){
        viewModelScope.launch {
            Log.i(TAG, "Fetching characters")
            _responseState.value = CharactersState.Loading
            try {
                _responseState.value = CharactersState.Success(
                    RNMApi.retrofitService.getCharacters(page, name, status, species, type, gender)
                )
                Log.i(TAG, responseState.value.toString())
            } catch (e: Exception){
                Log.w(TAG, e)
                _responseState.value = CharactersState.Error(e)
            }
        }
    }
}