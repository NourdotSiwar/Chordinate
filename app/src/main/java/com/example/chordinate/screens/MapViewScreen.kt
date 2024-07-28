package com.example.chordinate.screens


import MyBroadcastReceiver
import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.LocationDisplay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.arcgismaps.toolkit.geoviewcompose.MapViewScope
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay
import com.arcgismaps.toolkit.geoviewcompose.theme.CalloutDefaults
import com.example.chordinate.R
import com.example.chordinate.viewmodel.MapViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

// This file controls the UI/Layout
@Composable
fun MapViewScreen() {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val mapViewModel: MapViewModel = viewModel()
    val snackbarHostState = remember { mapViewModel.snackbarHostState }
    val application = LocalContext.current.applicationContext as Application
    val locationDisplay = rememberLocationDisplay()

    if (checkPermissions(context)) {
        // Permissions are already granted.
        LaunchedEffect(Unit) {
            locationDisplay.dataSource.start()
        }
    } else {
        RequestPermissions(
            onPermissionsGranted = {
                coroutineScope.launch {
                    locationDisplay.dataSource.start()
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                MapView(
                    modifier = Modifier.fillMaxWidth(),
                    mapViewProxy = mapViewModel.mapViewProxy,
                    arcGISMap = mapViewModel.map,
                    onSingleTapConfirmed = mapViewModel::identify,
                    locationDisplay = locationDisplay,
                    content = { getCalloutContent(mapViewModel) }
                )
                Row(
                    modifier = Modifier.Companion
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = { recenter(locationDisplay) },
                        modifier = Modifier.size(70.dp)
                    )
                    {
                        Image(
                            painter = painterResource(id = R.drawable.vinyl_option_2_orange),
                            contentDescription = null,
                            Modifier
                                .scale(1.2f, 1.2f)
                                .padding(top = 4.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    addSongPointToWebMap(
                        coroutineScope,
                        snackbarHostState,
                        mapViewModel,
                        locationDisplay,
                        application
                    )
                }, Modifier.padding(top = 8.dp)) {
                    Text("Add my song to the map!")
                }
            }
        }
    }
}

fun recenter(locationDisplay: LocationDisplay) {
    locationDisplay.setAutoPanMode(LocationDisplayAutoPanMode.Recenter)
}

private fun addSongPointToWebMap(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    mapViewModel: MapViewModel,
    locationDisplay: LocationDisplay,
    application: Application
) {
    coroutineScope.launch {
        MyBroadcastReceiver.SharedData?.let { info ->
            val attributes = mutableMapOf(
                "Artist" to info.artistName,
                "Song" to info.trackName,
                "Spotify_URI" to info.trackId,
                "Album" to info.albumName,
            )


            val featureTable = mapViewModel.serviceFeatureTable
            val mapLocation = locationDisplay.mapLocation
            if (mapLocation == null) {
                println("MapLocation is null or invalid")
                return@launch // or handle the error accordingly
            }

            featureTable.load().onSuccess {
                val feature = featureTable.createFeature(attributes, mapLocation)

                featureTable.addFeature(feature).onSuccess {
                    snackbarHostState.showSnackbar("Adding your song...")

                    featureTable.applyEdits().onSuccess {
                        snackbarHostState.showSnackbar("Saving to map ...")
                        locationDisplay.setAutoPanMode(LocationDisplayAutoPanMode.Recenter)
                        locationDisplay.showLocation = false
                        featureTable.loadOrRefreshFeatures(listOf(feature)).onSuccess {
                            println("Table has been refereshed")
                        }

                        mapViewModel.mapViewProxy.setViewpointAnimated(
                            Viewpoint(mapLocation.extent.center),
                            duration = 0.5.seconds
                        )
                    }.onFailure { error ->
                        snackbarHostState.showSnackbar(
                            "Error identifying results: ${error.message}. Cause:  ${error.cause}"
                        )
                        println("Error identifying results: ${error.message}. Cause:  ${error.cause}")
                    }
                }
            }
        }
    }
}

@Composable
private fun MapViewScope.getCalloutContent(mapViewModel: MapViewModel) {
    mapViewModel.selectedGeoElement?.let { geoElement ->
        Callout(
            modifier = Modifier.sizeIn(maxWidth = 250.dp),
            location = geoElement.geometry!!.extent.center,

            // Optional parameters to customize the callout appearance.
            shapes = CalloutDefaults.shapes(
                calloutContentPadding = PaddingValues(4.dp)
            ),
            colorScheme = CalloutDefaults.colors(
                backgroundColor = MaterialTheme.colorScheme.background,
                borderColor = MaterialTheme.colorScheme.outline
            )
        ) {
            // Callout content:
            Text(
                text = mapViewModel.calloutContent,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun RequestPermissions(onPermissionsGranted: () -> Unit) {
    // Create an activity result launcher using permissions contract and handle the result.
    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if both fine & coarse location permissions are true.
        if (permissions.all { it.value }) {
            onPermissionsGranted()
        } else {
            println("Location permissions were denied")
        }
    }

    LaunchedEffect(Unit) {
        activityResultLauncher.launch(
            // Request both fine and coarse location permissions.
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }
}

fun checkPermissions(context: Context): Boolean {
    val permissionCheckCoarseLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    // Fine location permission.
    val permissionCheckFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return permissionCheckCoarseLocation && permissionCheckFineLocation
}

