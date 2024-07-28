package com.example.chordinate

import MyBroadcastReceiver
import android.content.ContentValues.TAG
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.example.chordinate.ui.theme.AppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

//This file servers as main entry of program

class MainActivity : ComponentActivity() {

    // Launcher to handle the Spotify authentication result
    private lateinit var spotifyReciever: MyBroadcastReceiver
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        broadcastSetup()
        setApiKey()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Log.d(TAG, "onCreate: Starting MainActivity")

        // Set up the user interface
        setContent {
            AppTheme {
                Surface {
                    MainScreen(fusedLocationClient = fusedLocationClient)
                }
            }
        }
    }


    private fun setApiKey() {
        ArcGISEnvironment.apiKey =
            ApiKey.create("AAPK22bba5d8eb024b63b166b5e260536d7bnBKbDuc1HGVEPAjQ1NVHY6hUhEoLHMAD_ELwR75UqgYtgIf6S4GJe9umAXaOV6os")
    }

    private fun broadcastSetup() {
        spotifyReciever = MyBroadcastReceiver()
        val spotifyFilter = IntentFilter("com.spotify.music.playbackstatechanged")
        spotifyFilter.addAction("com.spotify.music.queuechanged")
        spotifyFilter.addAction("com.spotify.music.metadatachanged")
        val receiverFlags = ContextCompat.RECEIVER_EXPORTED

        ContextCompat.registerReceiver(this, spotifyReciever, spotifyFilter, receiverFlags)
    }
}


