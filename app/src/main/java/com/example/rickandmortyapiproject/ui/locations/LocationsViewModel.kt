package com.example.rickandmortyapiproject.ui.locations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.models.LocationsApiResponse
import com.example.rickandmortyapiproject.network.RNMApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "Locations Response"

class LocationsViewModel: ViewModel() {
    private val _responseState = MutableStateFlow<LocationsState>(LocationsState.Empty)
    val responseState = _responseState.asStateFlow()

    sealed class LocationsState {
        object Empty: LocationsState()
        object Loading: LocationsState()
        data class Success(val result: LocationsApiResponse): LocationsState()
        data class Error(val error: Throwable): LocationsState()
    }

    fun get(
        page: Int? = null,
        name: String? = null,
        type: String? = null,
        dimension: String? = null
    ){
        viewModelScope.launch {
            Log.i(TAG, "Fetching characters")
            _responseState.value = LocationsState.Loading
            try {
                _responseState.value = LocationsState.Success(
                    RNMApi.retrofitService.getLocations(page, name, type, dimension)
                )
                Log.i(TAG, responseState.value.toString())
            } catch (e: Exception){
                Log.w(TAG, e)
                _responseState.value = LocationsState.Error(e)
            }
        }
    }
}