package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.models.CharactersApiResponse
import com.example.rickandmortyapiproject.network.NetworkService
import com.example.rickandmortyapiproject.util.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CharactersListViewModel"

class CharactersViewModel : ViewModel() {
    private val _responseState = MutableStateFlow<DataState<CharactersApiResponse>>(DataState.Empty)
    val responseState = _responseState.asStateFlow()
    var nextUrl: String? = null

    fun get(
        name: String? = null,
        status: String? = null,
        species: String? = null,
        type: String? = null,
        gender: String? = null,
    ) {
        viewModelScope.launch {
            Log.i(TAG, "Fetching characters")
            _responseState.value = DataState.Loading
            try {
                _responseState.value = DataState.Success(
                    NetworkService.retrofitService.getCharacters(
                        name,
                        status,
                        species,
                        type,
                        gender
                    )
                )
                Log.i(TAG, responseState.value.toString())
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseState.value = DataState.Error(e)
            }
        }
    }

    fun getNext() {
        viewModelScope.launch {
            Log.i(TAG, "fetching next")
            _responseState.value = DataState.Loading
            try {
                _responseState.value = DataState.Success(
                    NetworkService.retrofitService.getCharacters(nextUrl!!)
                )
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseState.value = DataState.Error(e)
            }
        }
    }
}

//class CharactersViewModelFactory(
//    private val characterDao: CharacterDao
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(CharactersViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return CharactersViewModel(characterDao) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}