package com.example.chordinate

import MyBroadcastReceiver
import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcgismaps.data.Feature
import com.arcgismaps.data.QueryFeatureFields
import com.arcgismaps.data.QueryParameters
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay
import com.arcgismaps.toolkit.geoviewcompose.theme.CalloutDefaults
import kotlinx.coroutines.launch
import java.time.Instant

@Composable
fun MainScreen(onAuthorizeClick: () -> Unit, songInfo: String, isLoggedIn: Boolean) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var features by remember { mutableStateOf<List<Feature>>(emptyList()) }
    // Create and remember a location display with a recenter auto pan mode.
    val locationDisplay = rememberLocationDisplay().apply {
        setAutoPanMode(LocationDisplayAutoPanMode.Recenter)
    }
    val mapViewModel: MapViewModel = viewModel()

    val snackbarHostState = remember { mapViewModel.snackbarHostState }
    if (checkPermissions(context)) {
        // Permissions are already granted.
        LaunchedEffect(Unit) {
            locationDisplay.dataSource.start()
        }
    } else {
        RequestPermissions(
            context = context,
            onPermissionsGranted = {
                coroutineScope.launch {
                    locationDisplay.dataSource.start()
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { it ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                MapView(
                    modifier = Modifier.weight(1f),
                    mapViewProxy = mapViewModel.mapViewProxy,
                    arcGISMap = mapViewModel.map,
                    onSingleTapConfirmed = mapViewModel::identify,
                  //  locationDisplay = locationDisplay,
                    content = {
                        // Show a callout only when a lat/lon point is available.
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
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                    Button(onClick = {
                        coroutineScope.launch {
                            MyBroadcastReceiver.SharedData?.let { info ->
                                snackbarHostState.showSnackbar(
                                    "${info.artistName} wrote ${info.trackName}"
                                )
                                val attributes = mutableMapOf(
                                    "Artist" to info.artistName,
                                    "Song" to info.trackName,
                                    "Spotify_URI" to info.trackId,
                                    "Album" to info.albumName,
                                    "Longitude" to (locationDisplay.mapLocation?.x),
                                    "Latitude" to locationDisplay.mapLocation?.y,
                                )

                                val featureTable = mapViewModel.serviceFeatureTable

                                val mapLocation = locationDisplay.mapLocation
                                if (mapLocation == null) {
                                    println("MapLocation is null or invalid")
                                    return@launch // or handle the error accordingly
                                }

                                featureTable.load().onSuccess {
                                    // Create the feature with the provided attributes and location
                                    val feature = featureTable.createFeature(attributes, mapLocation)

                                    println("can add" + featureTable.canAdd().toString())
                                    println("can edit" + featureTable.isEditable.toString())
                                    // Add the feature to the feature table
                                    // Add the feature to the feature table
                                    featureTable.addFeature(feature).onSuccess {
                                        // Notify the user that the feature has been added
                                        println("${feature.attributes}")
                                        println("${feature.geometry?.extent?.center}")
                                        println("feature count: ${feature.featureTable?.numberOfFeatures}")
                                        featureTable.layer?.let { it1 ->
                                            mapViewModel.map.operationalLayers.add(
                                                it1
                                            )
                                        }
                                        featureTable.applyEdits().onSuccess {
                                            snackbarHostState.showSnackbar("Added ${feature.attributes}")
                                        }.onFailure {  e->
                                            snackbarHostState.showSnackbar("Error applying edits: ${e.message} ${e.cause}")
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
                    }) {
                        Text("Add my BR info to map!")
                    }
                    Button(onClick = {
                        coroutineScope.launch {
                            val queryParams = QueryParameters().apply {
                                whereClause = "1=1"
                            }
                            mapViewModel.serviceFeatureTable.queryFeatures(queryParams, QueryFeatureFields.LoadAll).onSuccess {  queryResult ->
                                features = queryResult.toList()

                                    features.forEach { feature ->
                                    println("FID: ${feature.attributes["FID"]}")
                                    println("Spotify_URI: ${feature.attributes["Spotify_URI"]}")
                                    println("Artist: ${feature.attributes["Artist"]}")
                                    println("Song: ${feature.attributes["Song"]}")
                                    println("Album: ${feature.attributes["Album"]}")
                                    println("Location: ${feature.geometry?.extent?.center}")
                                }
                            }.onFailure {
                                showError(context, it.message.toString())
                            }
                        }
                    }) {
                        Text("Show table")
                    }
                    LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                        items(features) { feature ->
                            Text(text = "# of features: ${feature.featureTable?.numberOfFeatures}")
                            FeatureRow(feature)
                        }
                    }
                    Text(
                        text = songInfo,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun FeatureRow(feature: Feature) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Song: ${feature.attributes["Song"]}")
        Text(text = "Album: ${feature.attributes["Album"]}")
        // Add more fields if necessary
    }
}

@Composable
fun RequestPermissions(context: Context, onPermissionsGranted: () -> Unit) {
    // Create an activity result launcher using permissions contract and handle the result.
    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if both fine & coarse location permissions are true.
        if (permissions.all { it.value }) {
            onPermissionsGranted()
        } else {
            showError(context, "Location permissions were denied")
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

fun showError(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
