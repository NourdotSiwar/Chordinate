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

package com.example.chordinit

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.PortalItem
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.layers.FeatureLayer
import com.arcgismaps.mapping.layers.Layer
import com.arcgismaps.mapping.view.SingleTapConfirmedEvent
import com.arcgismaps.portal.Portal
import com.arcgismaps.toolkit.geoviewcompose.MapViewProxy
import kotlinx.coroutines.launch

class MapViewModel(private val application: Application) : AndroidViewModel(application) {
    val mapViewProxy = MapViewProxy()

    // Keep track of the state of the callout content String.
    var calloutContent: String by mutableStateOf("")

    // Keep track of the currently selected GeoElement.
    var selectedGeoElement by mutableStateOf<Point?>(null)
        private set

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
        featureLayer?.let {
            viewModelScope.launch {
                mapViewProxy.identify(
                    featureLayer as Layer,
                    screenCoordinate = singleTapConfirmedEvent.screenCoordinate,
                    tolerance = 12.dp,
                    maximumResults = 1
                ).onSuccess { result ->
                    result.geoElements.firstOrNull()?.let { observation ->
                        selectedGeoElement = observation.geometry?.extent?.center
                    }
                    //snackbarHostState.showSnackbar("Success $result")
                    calloutContent = application.getString(
                        R.string.callout_text,
                        selectedGeoElement?.y,
                        selectedGeoElement?.x
                    )

                }.onFailure { error ->
                    snackbarHostState.showSnackbar(
                        "Error identifying results: ${error.message}. Cause:  ${error.cause}"
                    )
                }
            }
        }
    }

    private fun createMap(): ArcGISMap {

        val portal = Portal(
            url = "https://www.arcgis.com",
            connection = Portal.Connection.Anonymous
        )

        val portalItem = PortalItem(
            portal = portal,
            itemId = "2e4b3df6ba4b44969a3bc9827de746b3"
        )

        featureLayer = FeatureLayer.createWithItem(portalItem)

        return ArcGISMap(BasemapStyle.ArcGISTopographic).apply {
            initialViewpoint = Viewpoint(
                latitude = 34.0270,
                longitude = -118.8050,
                scale = 72000.0
            )

            operationalLayers.add(featureLayer!!)

        }

    }

}
