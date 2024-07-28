package com.example.chordinate


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay
import com.example.chordinate.navigation.BottomBar
import com.example.chordinate.navigation.BottomNavItem
import com.example.chordinate.navigation.Navigation
import com.example.chordinate.navigation.Screens
import com.example.chordinate.viewmodel.MapViewModel
import kotlinx.coroutines.launch
import com.google.android.gms.location.FusedLocationProviderClient

// This file controls the UI/Layout
@Composable
fun MainScreen(
    onAuthorizeClick: () -> Unit,
    songInfo: String,
    isLoggedIn: Boolean,
    fusedLocationClient: FusedLocationProviderClient
) {

    val mapViewModel: MapViewModel = viewModel()
    val snackbarHostState = remember { mapViewModel.snackbarHostState }

    val context = LocalContext.current
    ArcGISEnvironment.applicationContext = context.applicationContext
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem(Screens.MapView.screen, Screens.MapView.icon, Screens.MapView.name),
        BottomNavItem(
            Screens.RecPlaylist.screen,
            Screens.RecPlaylist.icon,
            Screens.RecPlaylist.name
        ),
        BottomNavItem(Screens.About.screen, Screens.About.icon, Screens.About.name),
    )

    val coroutineScope = rememberCoroutineScope()
    val locationDisplay = rememberLocationDisplay()
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
        //If you can change the padding maybe?  Or the size of the icon to make it appear as a banner....  right now it's so small
        topBar = {
            Column {
                getTopAppBar()
            }
        },

        bottomBar = { BottomBar(navController = navController, items = bottomNavItems) }
    ) { innerPadding ->
        Navigation(
            onAuthorizeClick,
            songInfo, isLoggedIn, navController,
            innerPadding, locationDisplay,
            fusedLocationClient
        )
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun getTopAppBar() {
    TopAppBar(
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.chordinate_one_line),
                contentDescription = null,
                Modifier.absoluteOffset(0.dp, 30.dp)
            )
        },
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
