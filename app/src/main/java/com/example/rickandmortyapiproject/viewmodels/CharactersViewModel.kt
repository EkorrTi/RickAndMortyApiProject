package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.database.CharacterDao
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.network.NetworkService
import com.example.rickandmortyapiproject.util.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CharactersListViewModel"

class CharactersViewModel(private val characterDao: CharacterDao) : ViewModel() {
    private val _responseState = MutableStateFlow<DataState<List<Character>>>(DataState.Empty)
    val responseState = _responseState.asStateFlow()
    var nextUrl: String? = null

    fun getFromDB(
        name: String? = null,
        status: String? = null,
        species: String? = null,
        type: String? = null,
        gender: String? = null,
    ) {
        viewModelScope.launch {
            Log.i(TAG, "Fetching from database")
            _responseState.value = DataState.Loading
            _responseState.value = DataState.Success(
                characterDao.getAll(name, status, species, type, gender)
            )
        }
    }

    private fun insertIntoDB(characters: List<Character>){
        viewModelScope.launch {
            characterDao.insert(characters)
        }
    }

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
                val response = NetworkService.retrofitService.getCharacters(name, status, species, type, gender)
                nextUrl = response.info.next
                _responseState.value = DataState.Success(response.results)
                insertIntoDB(response.results)
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
                val response = NetworkService.retrofitService.getCharacters(nextUrl!!)
                nextUrl = response.info.next
                _responseState.value = DataState.Success(response.results)
                insertIntoDB(response.results)
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseState.value = DataState.Error(e)
            }
        }
    }
}

class CharactersViewModelFactory(private val characterDao: CharacterDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharactersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharactersViewModel(characterDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}