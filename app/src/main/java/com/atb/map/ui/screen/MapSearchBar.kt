package com.atb.map.ui.screen

import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.atb.map.R
import com.mapbox.common.MapboxSDKCommon
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion

@SuppressLint("InvalidColorHexValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchBar(
    mapViewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapUiState by mapViewModel.mapUiState.collectAsState()
    var active by remember {
        mutableStateOf(false)
    }
    val placeAutocomplete = PlaceAutocomplete.create(context.getString(R.string.mapbox_access_token))


    LaunchedEffect(key1 = mapUiState.query) {
        val resultPlaceAutocomplete = placeAutocomplete.suggestions(mapUiState.query)
        if (resultPlaceAutocomplete.isValue) {
            mapViewModel.updateSuggestions(requireNotNull(resultPlaceAutocomplete.value))

        } else {
            Toast.makeText(context, "Check your connexion", Toast.LENGTH_LONG).show()
        }
    }

    SearchBar(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        colors = SearchBarDefaults.colors(Color(0xB3FFFFFF)),
        query = mapUiState.query,
        onQueryChange = {mapViewModel.updateQuery(it)},
        onSearch = {
            active = false
            mapViewModel.updateQuery("")
            if (mapUiState.searchSuggestions.isNotEmpty()) {
                mapViewModel.updatePoint(mapUiState.searchSuggestions.first().coordinate)
            }
        },
        active = active,
        onActiveChange = {active = it},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "search icon",
                tint = Color(0xFF006E6E)
            )
        },
        trailingIcon = {
            if (active) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close icon",
                    tint = Color(0xFF006E6E),
                    modifier = Modifier.clickable {
                        if (mapUiState.query.isNotBlank()) {
                            mapViewModel.updateQuery("")
                        } else {
                            active = false
                        }
                    }
                )

            }
        }
    ) {
        LazyColumn {
            items(mapUiState.searchSuggestions) {suggestion ->
                SuggestionItem(
                    suggestion = suggestion,
                    onSuggestionClick = {
                        mapViewModel.updatePoint(it.coordinate)
                        active = false
                        mapViewModel.updateQuery("")
                    }
                )
            }
        }
    }
}

@Composable
fun SuggestionItem(
    suggestion: PlaceAutocompleteSuggestion,
    onSuggestionClick: (PlaceAutocompleteSuggestion) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSuggestionClick(suggestion) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "location icon",
            tint = Color(0xFF006E6E)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = suggestion.name,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
