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
import kotlin.reflect.KFunction1
import com.example.chordinate.MapViewScreen
import com.example.chordinate.AboutScreen
import com.example.chordinate.recplaylist.RecPlaylistScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(onAuthorizeClick: () -> Unit, onMapRecenterClick: KFunction1<LocationDisplay, Unit>, songInfo: String, isLoggedIn: Boolean, navController: NavHostController = rememberNavController(), paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screens.MapScreen.screen,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screens.MapScreen.screen) {
            MapViewScreen()
        }
        composable(Screens.RecPlaylist.screen) {
            RecPlaylistScreen()
        }
        composable(Screens.About.screen) {
            AboutScreen()
        }

    }
}







