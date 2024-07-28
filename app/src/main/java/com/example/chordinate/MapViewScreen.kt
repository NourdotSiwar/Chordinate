package com.example.chordinate

import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.mapping.view.LocationDisplay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay
import com.arcgismaps.toolkit.geoviewcompose.theme.CalloutDefaults
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction1


@Composable
fun MapViewScreen() {

    val mapViewModel : MapViewModel = viewModel()
    //val snackbarHostState = remember { mapViewModel.snackbarHostState }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    ArcGISEnvironment.applicationContext = context.applicationContext

    val locationDisplay = rememberLocationDisplay().apply {
        setAutoPanMode(LocationDisplayAutoPanMode.Recenter)
    }

    if (checkPermissions(context)) {
        // Permissions are already granted.
        LaunchedEffect(Unit) {
            locationDisplay.dataSource.start()
        }
    } else {

        RequestPermissions(
            context = context,
            onPermissionsGranted = {
                coroutineScope.launch {
                    locationDisplay.dataSource.start()
                }
            }
        )
    }

        //content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    //.padding(it)
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
                        // TODO: Lots of hard-coded values here ...
                        Button(onClick = { recenter(locationDisplay) }, modifier=Modifier.offset(350.dp, 600.dp).size(70.dp))
                        {
                            Image(painter = painterResource(id = R.drawable.vinyl_option_2_orange), contentDescription =null, Modifier.scale(3.5f, 3.5f).offset(0.dp, 1.dp))
                        }
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    if (isLoggedIn) {
//                        Text(text = "Logged in with Spotify")
//                    } else {
//                        Button(onClick = onAuthorizeClick) {
//                            Text("Login with Spotify")
//                        }
//                    }
                    Text(
                        text = "songInfo",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        //}
}

fun recenter(locationDisplay: LocationDisplay)
{
    locationDisplay.setAutoPanMode(LocationDisplayAutoPanMode.Recenter)
}



