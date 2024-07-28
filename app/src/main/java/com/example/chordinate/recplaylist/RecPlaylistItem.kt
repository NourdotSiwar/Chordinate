package com.example.chordinate.recplaylist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

@Composable
fun RecPlaylistItem(songInfo: RecPlaylistViewModel.SongInfo) {
    Column {
        Text (
            text = songInfo.name,
        )
        Text (
            text = songInfo.artist,
        )
        Text (
            text = songInfo.album,
        )
        Text (
            text = songInfo.song_id,
        )
    }
}