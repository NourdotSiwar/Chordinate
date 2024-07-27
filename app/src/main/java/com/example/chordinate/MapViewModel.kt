/* Copyright 2024 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.chordinate

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.GeoElement
import com.arcgismaps.mapping.PortalItem
import com.arcgismaps.mapping.layers.FeatureLayer
import com.arcgismaps.mapping.view.SingleTapConfirmedEvent
import com.arcgismaps.portal.Portal
import com.arcgismaps.toolkit.geoviewcompose.MapViewProxy
import com.example.chordinate.R
import kotlinx.coroutines.launch

class MapViewModel(private val application: Application) : AndroidViewModel(application) {
    val mapViewProxy = MapViewProxy()

    // Keep track of the state of the callout content String.
    var calloutContent: String by mutableStateOf("")

    var selectedGeoElement by mutableStateOf<GeoElement?>(null)

    val map = createMap()

    val snackbarHostState = SnackbarHostState()

    private var featureLayer: FeatureLayer? = null

    init {
        viewModelScope.launch {
            map.load().onSuccess {
                snackbarHostState.showSnackbar("Map loaded")
            }.onFailure { e ->
                snackbarHostState.showSnackbar("Map did not load ${e.cause} ")
            }
        }
    }

    fun identify(singleTapConfirmedEvent: SingleTapConfirmedEvent) {
        viewModelScope.launch {
            mapViewProxy.identifyLayers(
                screenCoordinate = singleTapConfirmedEvent.screenCoordinate,
                tolerance = 12.dp,
                maximumResults = 1
            ).onSuccess { results ->
                if (results.isNotEmpty()) {

                    results.first().geoElements.firstOrNull()?.let { observation ->
                        selectedGeoElement = observation
                    }
                    //snackbarHostState.showSnackbar("Success $result")
                    calloutContent = application.getString(
                        R.string.callout_text,
                        selectedGeoElement?.attributes?.getOrDefault("Song", "No song found"),
                        selectedGeoElement?.attributes?.getOrDefault("Album_Playlist", "No album found")
                    )
                }

            }.onFailure { error ->
                snackbarHostState.showSnackbar(
                    "Error identifying results: ${error.message}. Cause:  ${error.cause}"
                )
            }
        }
    }

    private fun createMap(): ArcGISMap {

        val portal = Portal(
            url = "https://intern-hackathon.maps.arcgis.com/",
            connection = Portal.Connection.Anonymous
        )

        val portalItem = PortalItem(
            portal = portal,
            itemId = "7f954da98e2c42d099af1632d2cb6d65"
        )

        featureLayer = FeatureLayer.createWithItemAndLayerId(portalItem, 0)

        return ArcGISMap(portalItem)
    }
}
