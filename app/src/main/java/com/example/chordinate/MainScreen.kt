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
import android.media.Image
import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.arcgismaps.mapping.view.LocationDisplay
import com.example.chordinate.navigation.BottomNavItem
import com.example.chordinate.navigation.Navigation
import com.example.chordinate.navigation.Screens
import com.example.uceyecomposeversion.ui.components.BottomBar
import com.google.android.gms.location.FusedLocationProviderClient
import kotlin.reflect.KFunction1

// This file controls the UI/Layout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onAuthorizeClick: () -> Unit, onMapRecenterClick: KFunction1<LocationDisplay, Unit>, songInfo: String, isLoggedIn: Boolean, fusedLocationClient: FusedLocationProviderClient) {

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

    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem(Screens.MapScreen.screen, Screens.MapScreen.icon, Screens.MapScreen.name ),
        BottomNavItem(Screens.RecPlaylist.screen, Screens.RecPlaylist.icon, Screens.RecPlaylist.name ),
        BottomNavItem(Screens.About.screen, Screens.About.icon, Screens.About.name ),
        )

    val topNavItems = listOf(
        BottomNavItem(Screens.MapScreen.screen, R.drawable.chordinate_one_line, Screens.MapScreen.name)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        //If you can change the padding maybe?  Or the size of the icon to make it appear as a banner....  right now it's so small
        topBar = {
            Column {
                TopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        Image(painter = painterResource(id = R.drawable.chordinate_one_line), contentDescription =null, Modifier.absoluteOffset(0.dp, 30.dp)
                        )
                    },
                )
            }
       },

        bottomBar = { BottomBar(navController = navController, items = bottomNavItems) }
        ) { innerPadding ->
            Navigation(navController, innerPadding, locationDisplay, fusedLocationClient)
            }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordinateAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { "Chordinate" },
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
