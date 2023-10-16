package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.models.Location
import com.example.rickandmortyapiproject.network.NetworkService
import com.example.rickandmortyapiproject.util.DataState
import com.example.rickandmortyapiproject.util.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "LocationDetail"

class LocationDetailsViewModel : ViewModel() {
    private val _responseLocationState =
        MutableStateFlow<DataState<Location>>(DataState.Empty)
    val responseLocationState = _responseLocationState.asStateFlow()
    private val _responseResidentsState =
        MutableStateFlow<DataState<List<Character>>>(DataState.Empty)
    val responseResidentsState = _responseResidentsState.asStateFlow()

    fun getLocation(id: Int) {
        viewModelScope.launch {
            Log.i(TAG, "sending the request")
            _responseLocationState.value = DataState.Loading
            try {
                val loc = NetworkService.retrofitService.getLocation(id)
                _responseLocationState.value = DataState.Success(loc)
                Log.i(TAG, responseLocationState.value.toString())
                getResidents(loc.residents)
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseLocationState.value = DataState.Error(e)
            }
        }
    }

    private fun getResidents(characters: List<String>) {
        if (characters.isEmpty()){
            Log.i(TAG, "Residents empty")
            _responseResidentsState.value = DataState.Success(emptyList())
            return
        }
        val ids = Utils.extractCharacterIds(characters)
        Log.i(TAG, "Extracted episode ids: $ids, size: ${ids.size}")

        viewModelScope.launch {
            _responseResidentsState.value = DataState.Loading
            try {
                _responseResidentsState.value = DataState.Success(
                    NetworkService.retrofitService.getCharactersList(ids)
                )
                Log.i(TAG, "got residents")
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseResidentsState.value = DataState.Error(e)
            }
        }
    }
}