package com.atb.map.ui.screen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.graphics.drawable.toBitmap
import com.atb.map.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

@SuppressLint("UseCompatLoadingForDrawables")
@Composable
fun MyMap(
    modifier: Modifier = Modifier,
    point: Point?,
    onPointChange: (Point) -> Unit
) {
    val context = LocalContext.current
    val marker = remember {
        context.getDrawable(R.drawable.location_on)!!.toBitmap()
    }
    var pointAnnotationManager: PointAnnotationManager? by remember {
        mutableStateOf(null)
    }

    AndroidView(
        factory = {
            MapView(it).also {mapView ->
                mapView.getMapboxMap().also {mapboxMap ->
                    mapboxMap.loadStyleUri(Style.MAPBOX_STREETS)
                    mapboxMap.addOnMapClickListener {pt ->
                        onPointChange(pt)
                        true
                    }
                }
                pointAnnotationManager = mapView.annotations.createPointAnnotationManager()
            }
        },
        update = {mapView ->
            if (point != null) {
                pointAnnotationManager?.let {
                    it.deleteAll()
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(marker)
                    it.create(pointAnnotationOptions)

                    mapView.getMapboxMap().flyTo(CameraOptions.Builder().zoom(15.0).center(point).build())
                }
            }
            NoOpUpdate
        },
        modifier = modifier
    )
}