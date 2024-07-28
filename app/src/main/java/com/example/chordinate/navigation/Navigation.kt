package com.example.chordinate.navigation

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

@Composable
fun Navigation(
    onAuthorizeClick: () -> Unit,
    songInfo: String,
    isLoggedIn: Boolean,
    navController: NavHostController = rememberNavController(),
    paddingValues: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = Screens.MapView.screen,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screens.MapView.screen) {
            MapViewScreen(
                onAuthorizeClick = onAuthorizeClick,
                songInfo = songInfo,
                isLoggedIn = isLoggedIn
            )
        }
        composable(Screens.RecPlaylist.screen) {
            RecPlaylistScreen()
        }
        composable(Screens.About.screen) {
            AboutScreen()
        }

    }
}







