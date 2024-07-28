package com.example.chordinate.recplaylist

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.launch


class RecPlaylistViewModel(private val application: Application) : AndroidViewModel(application) {
    private var serviceTable: ServiceFeatureTable = createServiceTable()

    var songList = mutableStateListOf<SongInfo>()
    var loadStatus = mutableStateOf("Starting...")

    private val _locationDisplayState = MutableStateFlow<LocationDisplay?>(null)
    val locationDisplayState: StateFlow<LocationDisplay?> = _locationDisplayState

    fun updateLocationDisplay(locationDisplay: LocationDisplay) {
        _locationDisplayState.value = locationDisplay
    }

    private var locationDisplay: LocationDisplay? = null

    init {
    }

    public fun setLocationDisplay(locationDisplay: LocationDisplay) {
        this.locationDisplay = locationDisplay
    }

    private fun createServiceTable(): ServiceFeatureTable {
        return ServiceFeatureTable(
            uri = "https://services8.arcgis.com/LLNIdHmmdjO2qQ5q/ArcGIS/rest/services/NewMockData/FeatureServer/0"
        )
    }


    public fun refreshLocalPlaylist(
        radius: Double = 1000.0
    ) {
        viewModelScope.launch {
            if (locationDisplay==null || locationDisplay?.mapLocation==null) {
                loadStatus.value = "Location not found."
            } else if (locationDisplay?.mapLocation!!.x==0.0 && locationDisplay?.mapLocation!!.y==0.0) {
                loadStatus.value = "Location not yet plotted. Please open the map and wait for the blue dot to load."
            } else {
                Log.d("MyLocation", "${locationDisplay?.mapLocation!!.x}, ${locationDisplay?.mapLocation}")
                doRefreshPlaylist(locationDisplay?.mapLocation!!, radius)
            }
        }
    }


    private suspend fun refreshLocalPlaylist(
        center: Point,
        radius: Double = 1000.0
    ) {
        viewModelScope.launch {
            doRefreshPlaylist(center, radius)
        }
    }


    private suspend fun doRefreshPlaylist(center: Point, radius: Double) {
        loadStatus.value = "Loading..."

        val allSongsList = ArrayList<SongInfo>()

        val areaBuffer = GeometryEngine.bufferGeodeticOrNull(
            center,
            radius,
            LinearUnit.meters,
            Double.NaN,
            GeodeticCurveType.Geodesic
        )

        areaBuffer.let {
            val queryParams = QueryParameters()
            queryParams.geometry = areaBuffer
            queryParams.spatialRelationship = SpatialRelationship.Contains

            serviceTable.queryFeatures(queryParams, QueryFeatureFields.LoadAll).onSuccess { queryResult ->
                Log.d("Local playlist query", queryResult.toString())
                for (feature in queryResult) {
                    val songInfo = SongInfo(
                        name = feature.attributes.getOrDefault("Song", "") as String,
                        artist = feature.attributes.getOrDefault(
                            "Album_Playlist",
                            ""
                        ) as String,
                        album = feature.attributes.getOrDefault("Album", "") as String,
                        song_id = feature.attributes.getOrDefault("SpotifyURI", "") as String,
                    )
                    allSongsList.add(songInfo)
                }

                songList.clear()
                songList.addAll(allSongsList)

                if (songList.isEmpty()) {
                    loadStatus.value = "No songs found. Try increasing the search radius."
                }
            }
        }
    }

    data class SongInfo(val name: String, val artist: String, val album: String, val song_id: String)
}