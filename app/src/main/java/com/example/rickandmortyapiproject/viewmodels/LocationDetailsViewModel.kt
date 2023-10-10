package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.models.Character
import com.example.rickandmortyapiproject.models.Location
import com.example.rickandmortyapiproject.network.RNMApi
import com.example.rickandmortyapiproject.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "LocationDetail"

class LocationDetailsViewModel : ViewModel() {
    private val _responseLocationState =
        MutableStateFlow<LocationDetailState>(LocationDetailState.Empty)
    val responseLocationState = _responseLocationState.asStateFlow()
    private val _responseResidentsState =
        MutableStateFlow<LocationDetailState>(LocationDetailState.Empty)
    val responseResidentsState = _responseResidentsState.asStateFlow()

    sealed class LocationDetailState {
        object Empty : LocationDetailState()
        object Loading : LocationDetailState()
        data class Success(val result: Location) : LocationDetailState()
        data class SuccessResidents(val result: List<Character>) : LocationDetailState()
        object NoResidents : LocationDetailState()
        data class Error(val error: Throwable) : LocationDetailState()
    }

    fun getLocation(id: Int) {
        viewModelScope.launch {
            Log.i(TAG, "sending the request")
            _responseLocationState.value = LocationDetailState.Loading
            try {
                _responseLocationState.value = LocationDetailState.Success(
                    RNMApi.retrofitService.getLocation(id)
                )
                Log.i(TAG, responseLocationState.value.toString())
                getResidents(
                    (_responseLocationState.value as LocationDetailState.Success).result.residents
                )
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseLocationState.value = LocationDetailState.Error(e)
            }
        }
    }

    private fun getResidents(characters: List<String>) {
        if (characters.isEmpty()){
            Log.i(TAG, "Residents empty")
            _responseResidentsState.value = LocationDetailState.NoResidents
            return
        }
        val ids = mutableListOf<Int>()
        for (c in characters) ids.add(c.substring(Utils.ID_START_INDEX_CHARACTER).toInt())
        Log.i(TAG, "Extracted episode ids: $ids, size: ${ids.size}")

        viewModelScope.launch {
            _responseResidentsState.value = LocationDetailState.Loading
            try {
                _responseResidentsState.value = LocationDetailState.SuccessResidents(
                    RNMApi.retrofitService.getCharactersList(ids)
                )
                Log.i(TAG, "got residents")
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseResidentsState.value = LocationDetailState.Error(e)
            }
        }
    }
}