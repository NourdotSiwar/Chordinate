package com.example.chordinate.navigation

import android.app.Activity.MODE_PRIVATE
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.mapping.view.LocationDisplay
import com.example.chordinate.MainActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.reflect.KFunction1
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.chordinate.MainActivity.SpotifyConstants
import com.example.chordinate.ui.theme.AppTheme

private lateinit var spotifyAuthLauncher: ActivityResultLauncher<Intent>


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(onAuthorizeClick: () -> Unit, onMapRecenterClick: KFunction1<LocationDisplay, Unit>, songInfo: String, isLoggedIn: Boolean, navController: NavHostController = rememberNavController(), paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screens.MapScreen.screen,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screens.About.screen) {
            AboutScreen()
        }
        composable(Screens.MapScreen.screen) {
            MapViewScreen()
        }
        composable(Screens.RecPlaylist.screen) {
            RecPLaylistScreen()
        }
    }
}

@Composable
fun MapViewScreen() {
    Button(onClick = { /*TODO*/ }) {
        Text(text = "Map View Screen")
    }
}


@Composable
fun AboutScreen() {
    Button(onClick = { /*TODO*/ }) {
      Text(text = "About screen")
    }
}

@Composable
fun RecPLaylistScreen() {
    Button(onClick = { /*TODO*/ }) {
        Text(text = "Rec PLaylist screen")
    }
}


