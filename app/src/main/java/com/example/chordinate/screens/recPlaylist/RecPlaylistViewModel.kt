package com.example.chordinate.recplaylist

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
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
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate


class RecPlaylistViewModel(private val application: Application) : AndroidViewModel(application) {
    private var serviceTable: ServiceFeatureTable = createServiceTable()

    var songList = mutableStateListOf<SongInfoCount>()
    var loadStatus = mutableStateOf(PlaylistLoadStatus.STARTING)

    private var fusedLocationClient: FusedLocationProviderClient? = null

    fun setFusedLocationProvider(fusedLocationClient: FusedLocationProviderClient) {
        this.fusedLocationClient = fusedLocationClient
    }

    private fun createServiceTable(): ServiceFeatureTable {
        return ServiceFeatureTable(
            uri = "https://services8.arcgis.com/LLNIdHmmdjO2qQ5q/ArcGIS/rest/services/NewMockData/FeatureServer/0"
        )
    }


    @SuppressLint("MissingPermission")
    public fun refreshLocalPlaylist(
        radius: Double = 10000.0
    ) {
        if (fusedLocationClient == null) {
            loadStatus.value = PlaylistLoadStatus.NO_LOCATION_PROVIDER
        }
        else if (!checkLocationPermissions()) {
            loadStatus.value = PlaylistLoadStatus.NO_PERMISSIONS
        } else {
            fusedLocationClient!!.lastLocation.addOnSuccessListener { location: Location? ->
                viewModelScope.launch {
                    location?.let { loc: Location ->
                        var point = projectWgs(loc.longitude, loc.latitude)
                        doRefreshPlaylist(point, radius)
                    }
                }
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun projectWgs(lon: Double, lat: Double): Point {
        val crsFactory = CRSFactory()
        val WGS84 = crsFactory.createFromName("epsg:4326")
        val WgsWebMerc = crsFactory.createFromName("epsg:3857")

        val ctFactory = CoordinateTransformFactory()
        val wgsToWgsWebMerc = ctFactory.createTransform(WGS84, WgsWebMerc)

        val result = ProjCoordinate()
        wgsToWgsWebMerc.transform(ProjCoordinate(lon, lat), result)

        return Point(result.x, result.y, SpatialReference(3857))
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
        loadStatus.value = PlaylistLoadStatus.LOADING

        var allSongsList: ArrayList<SongInfoCount>

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

            serviceTable.queryFeatures(queryParams, QueryFeatureFields.LoadAll)
                .onSuccess { queryResult ->
                    val songInfoCounts = HashMap<String, SongInfoCount>()

                    for (feature in queryResult) {
                        val songId = feature.attributes.getOrDefault("Spotify_URI", "") as String

                        if (songId == "")
                            continue

                        if (songInfoCounts.containsKey(songId)) {
                            songInfoCounts[songId]!!.count++
                        } else {
                            val songInfo = SongInfo(
                                name = feature.attributes.getOrDefault("Song", "") as String,
                                artist = feature.attributes.getOrDefault("Artist", "") as String,
                                album = feature.attributes.getOrDefault("Album", "") as String,
                                song_id = feature.attributes.getOrDefault("Spotify_URI", "") as String,
                            )

                            songInfoCounts[songId] = SongInfoCount(songInfo)
                        }

                        allSongsList = ArrayList(songInfoCounts.values)
                        allSongsList.sortBy { item ->
                            -item.count
                        }

                        songList.clear()
                        songList.addAll(allSongsList)

                        if (songList.isEmpty()) {
                            loadStatus.value = PlaylistLoadStatus.EMPTY
                        }
                        else {
                            loadStatus.value = PlaylistLoadStatus.LOADED
                        }
                    }
                }
        }
    }

    data class SongInfo(
        val name: String,
        val artist: String,
        val album: String,
        val song_id: String
    )

    class SongInfoCount(songInfo: SongInfo, count: Int = 1) {
        val songInfo = songInfo
        var count = count

        companion object {
            const val SPOTIFY_TRACK_URI = "https://open.spotify.com/track/"
        }

        fun getUri(): String {
            var song_info_fixed = songInfo.song_id.replace("spotify:track:", "")
            return SPOTIFY_TRACK_URI + song_info_fixedd
        }
    }


    enum class PlaylistLoadStatus {
        STARTING,
        LOADING,
        LOADED,
        EMPTY,
        NO_PERMISSIONS,
        NO_LOCATION_PROVIDER
    }

    fun getLoadMessage(): String {
        return when (loadStatus.value) {
            PlaylistLoadStatus.STARTING -> "Starting..."
            PlaylistLoadStatus.LOADING -> "Querying map..."
            PlaylistLoadStatus.EMPTY -> "No features found. Try increasing your search radius!"
            PlaylistLoadStatus.NO_PERMISSIONS -> "Permissions not enabled. Please reload app and enable location permissions."
            PlaylistLoadStatus.NO_LOCATION_PROVIDER -> "Debug: Location provider is null"
            PlaylistLoadStatus.LOADED -> "You shouldn't be seeing this"
        }
    }
}