package com.example.chordinate.screens.recPlaylist

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.chordinate.recplaylist.RecPlaylistViewModel

@Composable
fun RecPlaylistItem(songItem: RecPlaylistViewModel.SongInfoCount) {
    val context = LocalContext.current

    Row(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = songItem.songInfo.name, style = MaterialTheme.typography.labelLarge)
            Text(text = songItem.songInfo.artist, style = MaterialTheme.typography.labelMedium)
            Text(text = songItem.songInfo.album, style = MaterialTheme.typography.labelMedium)
            Text(text = "Count: ${songItem.count}", style = MaterialTheme.typography.labelMedium)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            // Use Spotify URI scheme
            val uri =
                "spotify:track:${songItem.songInfo.song_id}" // Make sure to have the correct field in RecPlaylistItem
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            context.startActivity(intent)
        }) {
            Text("Open in Spotify")
        }
    }
}