package com.example.chordinate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.mapping.view.LocationDisplay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay
import com.arcgismaps.toolkit.geoviewcompose.theme.CalloutDefaults
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction1


@Composable
fun MapViewScreen(locationDisplay: LocationDisplay) {
    val mapViewModel : MapViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MapView(
            modifier = Modifier.weight(1f),
            mapViewProxy = mapViewModel.mapViewProxy,
            arcGISMap = mapViewModel.map,
            onSingleTapConfirmed = mapViewModel::identify,
            locationDisplay = locationDisplay,
            content = {
                // Show a callout only when a lat/lon point is available.
                mapViewModel.selectedGeoElement?.let { geoElement ->
                    Callout(
                        modifier = Modifier.sizeIn(maxWidth = 250.dp),
                        location = geoElement.geometry!!.extent.center,

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
    }
}
