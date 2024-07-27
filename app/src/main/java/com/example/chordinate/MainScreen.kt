package com.example.chordinate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.arcgismaps.toolkit.geoviewcompose.theme.CalloutDefaults

// This file controls the UI/Layout

@Composable
fun MainScreen(onAuthorizeClick: () -> Unit, songInfo: String) {

    val mapViewModel: MapViewModel = viewModel()
    val snackbarHostState = remember { mapViewModel.snackbarHostState }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                MapView(
                    modifier = Modifier.weight(1f),
                    mapViewProxy = mapViewModel.mapViewProxy,
                    arcGISMap = mapViewModel.map,
                    onSingleTapConfirmed = mapViewModel::identify,
                    content = {
                        // Show a callout only when a lat/lon point is available.
                        mapViewModel.selectedGeoElement?.let { point ->
                            Callout(
                                modifier = Modifier.sizeIn(maxWidth = 250.dp),
                                location = point,
                                // Optional parameters to customize the callout appearance.
                                shapes = CalloutDefaults.shapes(
                                    calloutContentPadding = PaddingValues(4.dp)
                                ),
                                colorScheme = CalloutDefaults.colors(
                                    backgroundColor = MaterialTheme.colorScheme.background,
                                    borderColor = MaterialTheme.colorScheme.outline
                                )
                            ) {
                                // Callout content:
                                Text(
                                    text = mapViewModel.calloutContent,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { onAuthorizeClick() }) {
                        Text("Login with Spotify")
                    }
                    Text(
                        text = songInfo,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    )
}

