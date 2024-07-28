package com.example.chordinate.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.chordinate.R


sealed class Screens(val screen: String = "", val icon: Int = 0, val name: String = "") {
    data object MapScreen :
        Screens(screen = "mapscreen", icon = R.drawable.chordinate_earth , name = "map screen")
    data object RecPlaylist :
        Screens(screen = "recplaylist", icon = R.drawable.baseline_album_24, name = "rec playlist")
    data object About :
        Screens(screen = "about", icon = R.drawable.baseline_info_24, name = "about")

}