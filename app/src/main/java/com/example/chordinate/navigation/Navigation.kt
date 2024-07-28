package com.example.chordinate.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arcgismaps.mapping.view.LocationDisplay
import com.example.chordinate.recplaylist.RecPlaylistScreen
import com.example.chordinate.screens.AboutScreen
import com.example.chordinate.screens.MapViewScreen
import kotlin.reflect.KFunction1
import com.example.chordinate.MapViewScreen
import com.example.chordinate.AboutScreen
import com.example.chordinate.recplaylist.RecPlaylistScreen
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun Navigation(
    onAuthorizeClick: () -> Unit,
    songInfo: String,
    isLoggedIn: Boolean,
    navController: NavHostController = rememberNavController(),
    paddingValues: PaddingValues,
    locationDisplay: LocationDisplay,
    fusedLocationClient: FusedLocationProviderClient
) {
    NavHost(
        navController = navController,
        startDestination = Screens.MapScreen.screen,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screens.MapView.screen) {
            MapViewScreen(
                onAuthorizeClick = onAuthorizeClick,
                songInfo = songInfo,
                isLoggedIn = isLoggedIn,
                locationDisplay = locationDisplay
            )
        }
        composable(Screens.RecPlaylist.screen) {
            RecPlaylistScreen(fusedLocationCLient)
        }
        composable(Screens.About.screen) {
            AboutScreen()
        }

    }
}







