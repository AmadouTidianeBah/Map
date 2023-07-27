package com.atb.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.atb.map.ui.screen.MapScreen
import com.atb.map.ui.screen.MapViewModel

@Composable
fun MapApp(
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel = remember { MapViewModel() }
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        MapScreen(mapViewModel = mapViewModel)
    }
}