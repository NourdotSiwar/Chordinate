package com.example.chordinate.screens


import MyBroadcastReceiver
import android.Manifest
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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
fun MapViewScreen(
    onAuthorizeClick: () -> Unit,
    songInfo: String,
    isLoggedIn: Boolean
) {

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
            MapView(
                modifier = Modifier.weight(1f),
                mapViewProxy = mapViewModel.mapViewProxy,
                arcGISMap = mapViewModel.map,
                onSingleTapConfirmed = mapViewModel::identify,
                locationDisplay = locationDisplay,
                content = {
                    // Show a callout only when a lat/lon point is available.
                    getCalloutContent(mapViewModel)
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoggedIn) {
                    Text(text = "Logged in with Spotify")
                } else {
                    Button(onClick = onAuthorizeClick) {
                        Text("Login with Spotify")
                    }
                }
                Text(
                    text = songInfo,
                    color = if (songInfo.contains("Currently playing: ")) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
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
                        mapViewModel.mapViewProxy.setViewpointAnimated(
                            Viewpoint(mapLocation), duration = 0.1.seconds
                        ).onSuccess {
                            val screenCoordinate = mapViewModel.mapViewProxy.locationToScreenOrNull(mapLocation)
                            locationDisplay.showLocation = false

                            if (screenCoordinate != null) {
                                mapViewModel.mapViewProxy.identifyLayers(
                                    screenCoordinate = screenCoordinate,
                                    tolerance = 12.dp,
                                    maximumResults = 1
                                ).onSuccess { results ->
                                    if (results.isNotEmpty()) {
                                        results.first().geoElements.firstOrNull()
                                            ?.let { observation ->
                                                mapViewModel.selectedGeoElement = observation
                                            }
                                        // snackbarHostState.showSnackbar("application: $application")
                                        mapViewModel.calloutContent = application.getString(
                                            R.string.callout_text,
                                            mapViewModel.selectedGeoElement?.attributes?.getOrDefault(
                                                "Song",
                                                "No song found"
                                            ),
                                            mapViewModel.selectedGeoElement?.attributes?.getOrDefault(
                                                "Album",
                                                "No album found"
                                            ),
                                            mapViewModel.selectedGeoElement?.attributes?.getOrDefault(
                                                "Artist",
                                                "No artist found"
                                            )
                                        )
                                    }

                                }.onFailure { error ->
                                    snackbarHostState.showSnackbar(
                                        "Error identifying results: ${error.message}. Cause:  ${error.cause}"
                                    )
                                    println("Error identifying results: ${error.message}. Cause:  ${error.cause}")
                                }
                            }
                        }.onFailure { e ->
                            snackbarHostState.showSnackbar("Error applying edits: ${e.message} ${e.cause}")
                        }
                    }

                }.onFailure {
                    // Handle failure to add the feature
                    Log.d(TAG, "Failed to add feature: ${it.cause}")
                }
            }.onFailure {
                // Handle failure to load the feature table
                Log.d(TAG, "Failed to load feature table: ${it.message}")
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
    // Check permissions to see if both permissions are granted.
    // Coarse location permission.
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

