package com.atb.map.ui.screen

import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModel: ViewModel() {
    private var _mapUiState = MutableStateFlow(MapUiState())
    val mapUiState: StateFlow<MapUiState> = _mapUiState.asStateFlow()

    fun updateQuery(query: String) {
        _mapUiState.update {
            it.copy(query = query)
        }
    }

    fun updatePoint(point: Point) {
        _mapUiState.update {
            it.copy(point = point)
        }
    }

    fun updateSuggestions(suggestions: List<PlaceAutocompleteSuggestion>) {
        _mapUiState.update {
            it.copy(searchSuggestions = suggestions)
        }
    }
}

data class MapUiState(
    val query: String = "",
    val point: Point? = null,
    val searchSuggestions: List<PlaceAutocompleteSuggestion> = emptyList()
)