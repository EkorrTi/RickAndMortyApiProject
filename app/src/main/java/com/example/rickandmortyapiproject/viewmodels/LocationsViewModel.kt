package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.models.Location
import com.example.rickandmortyapiproject.network.NetworkService
import com.example.rickandmortyapiproject.util.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "LocationsListViewModel"

class LocationsViewModel : ViewModel() {
    private val _responseState = MutableStateFlow<DataState<List<Location>>>(DataState.Empty)
    val responseState = _responseState.asStateFlow()
    var nextUrl: String? = null

    fun get(
        name: String? = null,
        type: String? = null,
        dimension: String? = null
    ) {
        viewModelScope.launch {
            Log.i(TAG, "Fetching characters")
            _responseState.value = DataState.Loading
            try {
                val response = NetworkService.retrofitService.getLocations(name, type, dimension)
                nextUrl = response.info.next
                _responseState.value = DataState.Success(response.results)
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
                val response = NetworkService.retrofitService.getLocations(nextUrl!!)
                nextUrl = response.info.next
                _responseState.value = DataState.Success(response.results)
            } catch (e: Exception) {
                Log.w(TAG, e)
                _responseState.value = DataState.Error(e)
            }
        }
    }
}