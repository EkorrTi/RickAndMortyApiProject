package com.example.rickandmortyapiproject.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapiproject.database.LocationDao
import com.example.rickandmortyapiproject.models.Location
import com.example.rickandmortyapiproject.network.NetworkService
import com.example.rickandmortyapiproject.util.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "LocationsListViewModel"

class LocationsViewModel(private val locationDao: LocationDao) : ViewModel() {
    private val _responseState = MutableStateFlow<DataState<List<Location>>>(DataState.Empty)
    val responseState = _responseState.asStateFlow()
    var nextUrl: String? = null

    fun getFromDB(
        name: String? = null,
        type: String? = null,
        dimension: String? = null
    ) {
        viewModelScope.launch {
            Log.i(TAG, "Fetching from database")
            _responseState.value = DataState.Loading
            _responseState.value = DataState.Success(
                locationDao.getAll(name, type, dimension)
            )
        }
    }

    private suspend fun insertIntoDB(locations: List<Location>) = locationDao.insert(locations)

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
                val response = NetworkService.retrofitService.getLocations(nextUrl!!)
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

class LocationsViewModelFactory(private val locationDao: LocationDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationsViewModel(locationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}