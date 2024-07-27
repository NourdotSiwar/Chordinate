package com.example.chordinate

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay
import com.arcgismaps.toolkit.geoviewcompose.theme.CalloutDefaults
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


import kotlinx.coroutines.launch
import android.Manifest
import com.arcgismaps.mapping.view.LocationDisplay
import kotlin.reflect.KFunction1

// This file controls the UI/Layout
@Composable
fun MainScreen(onAuthorizeClick: () -> Unit, onMapRecenterClick: KFunction1<LocationDisplay, Unit>, songInfo: String, isLoggedIn: Boolean, navController: NavHostController = rememberNavController()) {

    val mapViewModel: MapViewModel = viewModel()
    val snackbarHostState = remember { mapViewModel.snackbarHostState }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    ArcGISEnvironment.applicationContext = context.applicationContext

    // Create and remember a location display with a recenter auto pan mode.
    val locationDisplay = rememberLocationDisplay().apply {
        setAutoPanMode(LocationDisplayAutoPanMode.Recenter)
    }

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
        topBar = {
            ChordinateAppBar(canNavigateBack = false, navigateUp = { navController.navigateUp() })
        },
        content = {
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
                    locationDisplay = locationDisplay,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoggedIn) {
                        Text(text = "Logged in with Spotify")
                    } else {
                        Button(onClick = onAuthorizeClick) {
                            Text("Login with Spotify")
                        }
                    }
                    Button(onClick = { onMapRecenterClick(locationDisplay)}) {
                        Text("Re-center")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordinateAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("Chordinate") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
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
