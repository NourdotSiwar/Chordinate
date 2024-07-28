package com.example.chordinate

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcgismaps.ArcGISEnvironment
import androidx.navigation.compose.rememberNavController


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.ui.res.painterResource
import com.example.chordinate.navigation.BottomBar
import com.example.chordinate.navigation.BottomNavItem
import com.example.chordinate.navigation.Navigation
import com.example.chordinate.navigation.Screens
import com.example.chordinate.viewmodel.MapViewModel

// This file controls the UI/Layout
@Composable
fun MainScreen(
    onAuthorizeClick: () -> Unit,
    songInfo: String,
    isLoggedIn: Boolean
) {

    val mapViewModel: MapViewModel = viewModel()
    val snackbarHostState = remember { mapViewModel.snackbarHostState }
    val context = LocalContext.current

    ArcGISEnvironment.applicationContext = context.applicationContext


    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem(Screens.MapView.screen, Screens.MapView.icon, Screens.MapView.name ),
        BottomNavItem(Screens.RecPlaylist.screen, Screens.RecPlaylist.icon, Screens.RecPlaylist.name ),
        BottomNavItem(Screens.About.screen, Screens.About.icon, Screens.About.name ),
    )

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
        Navigation(onAuthorizeClick, songInfo, isLoggedIn, navController, innerPadding)
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


