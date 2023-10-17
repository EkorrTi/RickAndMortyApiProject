package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.database.CharacterDao
import com.example.rickandmortyapiproject.database.LocationDao
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.models.Location
import com.example.rickandmortyapiproject.network.NetworkService
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "LocationDetail"

class LocationDetailsViewModel(
    private val locationDao: LocationDao,
    private val characterDao: CharacterDao
) : ViewModel() {
    private val _responseLocationState =
        MutableStateFlow<DataState<Location>>(DataState.Empty)
    val responseLocationState = _responseLocationState.asStateFlow()
    private val _responseResidentsState =
        MutableStateFlow<DataState<List<Character>>>(DataState.Empty)
    val responseResidentsState = _responseResidentsState.asStateFlow()

    fun getLocation(id: Int, isConnected: Boolean) {
        viewModelScope.launch {
            Log.i(TAG, "sending the request")
            _responseLocationState.value = DataState.Loading
            try {
                val loc = if (isConnected)
                    NetworkService.retrofitService.getLocation(id)
                else
                    locationDao.getById(id)

                _responseLocationState.value = DataState.Success(loc)

                Log.i(TAG, responseLocationState.value.toString())
                getResidents(loc.residents, isConnected)
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseLocationState.value = DataState.Error(e)
            }
        }
    }

    private fun getResidents(characters: List<String>, isConnected: Boolean) {
        if (characters.isEmpty()) {
            Log.i(TAG, "Residents empty")
            _responseResidentsState.value = DataState.Success(emptyList())
            return
        }
        val ids = Utils.extractCharacterIds(characters)
        Log.i(TAG, "Extracted residents ids: $ids, size: ${ids.size}")

        viewModelScope.launch {
            _responseResidentsState.value = DataState.Loading
            try {
                if (isConnected) {
                    val response = NetworkService.retrofitService.getCharactersByIds(ids)
                    _responseResidentsState.value = DataState.Success(response)
                    characterDao.insert(response)
                } else
                    _responseResidentsState.value = DataState.Success( characterDao.getAllById(ids) )

                Log.i(TAG, "got residents")
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseResidentsState.value = DataState.Error(e)
            }
        }
    }
}

class LocationDetailsViewModelFactory(
    private val locationDao: LocationDao,
    private val characterDao: CharacterDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationDetailsViewModel(locationDao, characterDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}