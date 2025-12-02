package com.example.rgrmobiki.ui.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rgrmobiki.data.model.WeatherHistory
import com.example.rgrmobiki.data.repository.WeatherRepository
import com.example.rgrmobiki.domain.WeatherUiState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _history = MutableStateFlow<List<WeatherHistory>>(emptyList())
    val history: StateFlow<List<WeatherHistory>> = _history.asStateFlow()

    init {
        loadHistory()
    }

    fun searchWeather(cityName: String) {
        if (cityName.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            repository.getWeatherByCity(cityName).fold(
                onSuccess = { weather ->
                    _uiState.value = WeatherUiState.Success(weather)
                    loadHistory()
                },
                onFailure = { error ->
                    _uiState.value = WeatherUiState.Error(
                        error.message ?: "Помилка завантаження погоди"
                    )
                }
            )
        }
    }

    fun getWeatherByLocation(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _uiState.value = WeatherUiState.Error("Потрібен дозвіл на геолокацію")
            return
        }

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        viewModelScope.launch {
                            repository.getWeatherByCoordinates(it.latitude, it.longitude).fold(
                                onSuccess = { weather ->
                                    _uiState.value = WeatherUiState.Success(weather)
                                    loadHistory()
                                },
                                onFailure = { error ->
                                    _uiState.value = WeatherUiState.Error(
                                        error.message ?: "Помилка завантаження погоди"
                                    )
                                }
                            )
                        }
                    } ?: run {
                        _uiState.value = WeatherUiState.Error("Не вдалося отримати локацію")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Помилка")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun loadHistory() {
        viewModelScope.launch {
            repository.getWeatherHistory().collect { historyList ->
                _history.value = historyList
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}

