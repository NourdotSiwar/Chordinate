package com.example.chordinate.recplaylist

import RecPlaylistItem
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.data.QueryFeatureFields
import com.arcgismaps.data.QueryParameters
import com.arcgismaps.data.ServiceFeatureTable
import com.arcgismaps.data.SpatialRelationship
import com.arcgismaps.geometry.GeodeticCurveType
import com.arcgismaps.geometry.GeometryEngine
import com.arcgismaps.geometry.LinearUnit
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.view.LocationDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class RecPlaylistViewModel(
    application: Application,
    private val locationDisplay: LocationDisplay
) : AndroidViewModel(application) {

    private val _songList = MutableStateFlow<List<RecPlaylistItem>>(emptyList())
    val songList: StateFlow<List<RecPlaylistItem>> = _songList.asStateFlow()

    private val _loadStatus = MutableStateFlow("Starting...")
    val loadStatus: StateFlow<String> = _loadStatus.asStateFlow()

    private var serviceTable: ServiceFeatureTable = createServiceTable()

    private fun createServiceTable(): ServiceFeatureTable {
        return ServiceFeatureTable(
            uri = "https://services8.arcgis.com/LLNIdHmmdjO2qQ5q/ArcGIS/rest/services/NewMockData/FeatureServer/0"
        )
    }

    fun refreshLocalPlaylist(radius: Double = 10000.0) {
        viewModelScope.launch {
            if (locationDisplay.mapLocation == null) {
                _loadStatus.value = "Location not found."
            } else if (locationDisplay.mapLocation!!.x == 0.0 && locationDisplay.mapLocation!!.y == 0.0) {
                _loadStatus.value =
                    "Location not yet plotted. Please open the map and wait for the blue dot to load."
            } else {
                doRefreshPlaylist(locationDisplay.mapLocation!!, radius)
            }
        }
    }

    private suspend fun doRefreshPlaylist(center: Point, radius: Double) {
        val areaBuffer = GeometryEngine.bufferGeodeticOrNull(
            center,
            radius,
            LinearUnit.meters,
            Double.NaN,
            GeodeticCurveType.Geodesic
        )

        areaBuffer?.let {
            val queryParams = QueryParameters().apply {
                geometry = areaBuffer
                spatialRelationship = SpatialRelationship.Contains
            }

            serviceTable.queryFeatures(queryParams, QueryFeatureFields.LoadAll)
                .onSuccess { queryResult ->
                    val songInfoCounts = queryResult.groupBy { feature ->
                        feature.attributes.getOrDefault("Spotify_URI", "") as String
                    }.map { (_, features) ->
                        RecPlaylistItem(
                            track = features.first().attributes.getOrDefault("Song", "") as String,
                            artist = features.first().attributes.getOrDefault(
                                "Artist",
                                ""
                            ) as String,
                            album = features.first().attributes.getOrDefault("Album", "") as String,
                            count = features.size,
                            albumArtUrl = features.first().attributes.getOrDefault(
                                "AlbumArtUrl",
                                ""
                            ) as String
                        )
                    }.sortedByDescending { it.count }

                    _songList.value = songInfoCounts

                    if (songInfoCounts.isEmpty()) {
                        _loadStatus.value = "No songs found. Try increasing the search radius."
                    }
                }
        }
        _loadStatus.value = ""

    }

}