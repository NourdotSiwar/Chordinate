package com.example.chordinate.recplaylist

import RecPlaylistItem
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arcgismaps.mapping.view.LocationDisplay

@Composable
fun RecPlaylistScreen() {
    val context = LocalContext.current
    val recPlaylistViewModel = remember {
        RecPlaylistViewModel(application = context.applicationContext as Application)
    }
    val songList by recPlaylistViewModel.songList.collectAsState()
    val loadStatus by recPlaylistViewModel.loadStatus.collectAsState()
    LaunchedEffect(Unit) {
        recPlaylistViewModel.refreshLocalPlaylist()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = loadStatus, style = MaterialTheme.typography.labelSmall)

        LazyColumn {
            items(songList) { item ->
                SongListItem(songItem = item, context = context)
            }
        }
    }
}

@Composable
fun SongListItem(songItem: RecPlaylistItem, context: Context) {
    Row(modifier = Modifier.padding(8.dp)) {
        // Replace with actual UI elements for displaying the item
        Column(modifier = Modifier.weight(1f)) {
            Text(text = songItem.track, style = MaterialTheme.typography.labelSmall)
            Text(text = songItem.artist, style = MaterialTheme.typography.labelSmall)
            Text(text = songItem.album, style = MaterialTheme.typography.labelSmall)
            Text(text = "Count: ${songItem.count}", style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://open.spotify.com/track/${songItem.track}")
            )
            context.startActivity(intent)
        }) {
            Text("Open in Spotify")
        }
    }
}