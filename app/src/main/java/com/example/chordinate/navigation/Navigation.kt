package com.example.chordinate.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chordinate.screens.AboutScreen
import com.example.chordinate.screens.MapViewScreen
import com.example.chordinate.screens.recPlaylist.RecPlaylistScreen
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    paddingValues: PaddingValues,
    fusedLocationClient: FusedLocationProviderClient
) {
    NavHost(
        navController = navController,
        startDestination = Screens.MapView.screen,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screens.MapView.screen) {
            MapViewScreen()
        }
        composable(Screens.RecPlaylist.screen) {
            RecPlaylistScreen(fusedLocationClient)
        }
        composable(Screens.About.screen) {
            AboutScreen()
        }

    }
}






