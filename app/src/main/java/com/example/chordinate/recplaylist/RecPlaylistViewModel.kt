package com.example.chordinate.recplaylist

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarHostState
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
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.layers.FeatureLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecPlaylistViewModel(private val application: Application) : AndroidViewModel(application) {
    private var serviceTable: ServiceFeatureTable = createServiceTable()

    var songList = mutableStateListOf<SongInfo>()

    val snackbarHostState = SnackbarHostState()

    init {
        viewModelScope.launch {
            getLocalPlaylist(
                Point(-13046183.607400, 4036572.581340, spatialReference = SpatialReference(3857)))
        }

    }

    private fun createServiceTable(): ServiceFeatureTable {
        return ServiceFeatureTable(
            uri = "https://services8.arcgis.com/LLNIdHmmdjO2qQ5q/arcgis/rest/services/Map_WFL1/FeatureServer/0"
        )
    }


    private fun getLocalPlaylist(
        center: Point,
        radius: Double = 1000000.0
    ) {
        if (serviceTable == null) {
            Log.d("Local playlist query", "Feature layer featureTable does not exist")
            return
        }

        var allSongsList = ArrayList<SongInfo>()

        viewModelScope.launch(Dispatchers.IO) {
            var areaBuffer = GeometryEngine.bufferGeodeticOrNull(
                center,
                radius,
                LinearUnit.meters,
                Double.NaN,
                GeodeticCurveType.Geodesic
            )
            areaBuffer.let {
                var queryParams = QueryParameters()
                queryParams.whereClause = "1=1"
                queryParams.geometry = areaBuffer
                queryParams.spatialRelationship = SpatialRelationship.Contains
                serviceTable.queryFeatures(queryParams, QueryFeatureFields.LoadAll).onSuccess { queryResult ->
                    Log.d("Local playlist query", queryResult.toString())
                    for (feature in queryResult) {
                        var songInfo = SongInfo(
                            name = feature.attributes.getOrDefault("Song", "") as String,
                            artist = feature.attributes.getOrDefault("Album_Playlist", "") as String,
                            album = feature.attributes.getOrDefault("Album", "") as String,
                            song_id = feature.attributes.getOrDefault("SpotifyURI", "") as String,
                        )
                        allSongsList.add(songInfo)
                    }

                    songList.clear()
                    songList.addAll(allSongsList)
                }
            }
        }
    }

    data class SongInfo(val name: String, val artist: String, val album: String, val song_id: String)
}