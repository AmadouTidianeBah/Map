package com.atb.map.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mapbox.geojson.Point

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapUiState by mapViewModel.mapUiState.collectAsState()
    var relaunch by remember {
        mutableStateOf(false)
    }
    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (!permissions.values.all { it }) {
                //handle permission denied
            }
            else {
                relaunch = !relaunch
            }
        }
    )

    Box(modifier = modifier.fillMaxSize()) {
        MapSearchBar(
            mapViewModel = mapViewModel,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        MyMap(
            point = mapUiState.point,
            onPointChange = { mapViewModel.updatePoint(it) },
            modifier = Modifier.fillMaxSize()
        )
    }


    LaunchedEffect(key1 = relaunch) {
        try {
            val location = LocationService().getCurrentLocation(context)
            mapViewModel.updatePoint(Point.fromLngLat(location.longitude, location.latitude))
        } catch (e: LocationService.LocationServiceException) {
            when(e) {
                is LocationService.LocationServiceException.LocationDisabledException -> {}
                is LocationService.LocationServiceException.MissingPermissionException -> permissionRequest.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                is LocationService.LocationServiceException.NoInternetException -> {}
                is LocationService.LocationServiceException.UnknownException -> {}
            }
        }
    }
}