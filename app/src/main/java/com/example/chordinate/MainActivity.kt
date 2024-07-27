package com.example.chordinate

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
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

//This file servers as main entry of program

class MainActivity : ComponentActivity() {

    // Launcher to handle the Spotify authentication result
    private lateinit var spotifyAuthLauncher: ActivityResultLauncher<Intent>
    private var songInfo by mutableStateOf("No song info")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setApiKey()

        Log.d(TAG, "onCreate: Starting MainActivity")

        // Register launcher for handling activity result
        spotifyAuthLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val intent = result.data
                Log.d(TAG, "ActivityResult: resultCode=${result.resultCode}, intent=$intent")
                if (result.resultCode == RESULT_OK && intent != null) {
                    val response = AuthorizationClient.getResponse(result.resultCode, intent)
                    Log.d(
                        TAG,
                        "AuthorizationResponse: type=${response.type}, code=${response.code}, error=${response.error}, accessToken=${response.accessToken}"
                    )

                    when (response.type) {
                        AuthorizationResponse.Type.TOKEN -> {
                            // Successfully received access token
                            val accessToken = response.accessToken
                            Log.d(TAG, "Access Token: $accessToken")
                            // Handle access token (e.g., store it securely and proceed with authenticated actions)
                            fetchCurrentlyPlayingSong(accessToken)
                        }

                        AuthorizationResponse.Type.ERROR -> {
                            Log.e(TAG, "Error: ${response.error}")
                            songInfo = "Error: ${response.error}"
                        }

                        else -> {
                            // Other response types
                            Log.w(TAG, "Unhandled response type: ${response.type}")
                            songInfo = "Unhandled response type: ${response.type}"
                        }
                    }
                } else {
                    Log.e(TAG, "Intent was null or resultCode was not RESULT_OK")
                    // Handle cases where result is not OK
                    songInfo = "Failed to get result"
                }
            }

        // Set up the user interface
        setContent {
            MaterialTheme {
                Surface {
                    MainScreen(
                        onAuthorizeClick = ::authorizeWithSpotify, songInfo = songInfo
                    )
                }
            }
        }
    }

    private fun setApiKey() {
        ArcGISEnvironment.apiKey =
            ApiKey.create("AAPTxy8BH1VEsoebNVZXo8HurLlp_B8QY4A9dXRZiSER6g8HlIST8IkwpSOZmCHK0mizPjO06fEzeJTRSrG3kFYuTq05oefdr4wxDJ6D6jnTw1_FNbkIEgpyaEjXZ4yVdOYLbadNWPUPPhC1GhW9GloSoxEprhXSlHPt5B9kOygvAVoVu0EAvA62Atn9usD3ewfI9CYpX9gNWTXeBYy1WeLQXt6FM2yxMZAWZzfJstNLX89pZYf9-WIqSgnrRnuO1mKfAT1_xZUNSRfZ")
    }


    // Initiates Spotify authorization process
    private fun authorizeWithSpotify() {
        Log.d(TAG, "authorizeWithSpotify: Starting Spotify authorization")
        val builder = AuthorizationRequest.Builder(
            SpotifyConstants.CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            SpotifyConstants.REDIRECT_URI
        )

        builder.setScopes(arrayOf("user-read-playback-state"))
        val request = builder.build()

        val intent = AuthorizationClient.createLoginActivityIntent(this, request)
        spotifyAuthLauncher.launch(intent)
    }

    private fun fetchCurrentlyPlayingSong(accessToken: String) {
        // Create a request to fetch the currently playing song from the Spotify API
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/player/currently-playing")
            .addHeader(
                "Authorization",
                "Bearer $accessToken"
            ) // Include the access token in the Authorization header
            .build()

        // Create an OkHttpClient instance to handle the network request
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            // Called if the network request fails
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Network request failed", e)
                // Update UI with error message
                songInfo = "Network request failed: ${e.message}"
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Response Body: $responseBody")

                    if (responseBody.isNullOrEmpty()) {
                        Log.e(TAG, "Empty response body")
                        songInfo = "No song info available"
                        return
                    }

                    try {
                        val jsonResponse = JSONObject(responseBody)
                        val item = jsonResponse.optJSONObject("item")
                        if (item != null) {
                            val songName = item.optString("name", "Unknown song")
                            val artistName = item.optJSONArray("artists")?.optJSONObject(0)
                                ?.optString("name", "Unknown artist") ?: "Unknown artist"
                            songInfo = "Currently playing: $songName by $artistName"
                        } else {
                            Log.e(TAG, "Response does not contain 'item'")
                            songInfo = "No song info available"
                        }
                    } catch (e: JSONException) {
                        Log.e(TAG, "JSON parsing error", e)
                        songInfo = "Failed to parse song info"
                    }
                } else {
                    Log.e(TAG, "Failed response: ${response.code} ${response.message}")
                    songInfo = "Failed to fetch song info: ${response.message}"
                }
            }

        })
    }

    // Constants for Spotify authorization
    object SpotifyConstants {
        const val CLIENT_ID = "1b5da682077348aaa76e95aa58341492"
        const val REDIRECT_URI = "myapp://callback"
    }
}


