package com.example.chordinate.screens.recPlaylist

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalUriHandler
import com.example.chordinate.recplaylist.RecPlaylistViewModel

@Composable
fun RecPlaylistItem(songInfoCount: RecPlaylistViewModel.SongInfoCount) {
    val uriHandler = LocalUriHandler.current

    Column {
        Text (
            text = "Track: ${songInfoCount.songInfo.name}",
        )
        Text (
            text = "Artist: ${songInfoCount.songInfo.artist}",
        )
        Text (
            text = "Album: ${songInfoCount.songInfo.album}",
        )
        Text (
            text = "Count: ${songInfoCount.count}",
        )
        Button (onClick = {
            uriHandler.openUri(songInfoCount.getUri())
        }) {
            Text("Open in Spotify")
        }

    }
}