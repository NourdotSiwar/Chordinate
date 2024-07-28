package com.example.chordinate.recplaylist

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text

@Composable
fun RecPlaylistScreen() {
    val recPlaylistViewModel: RecPlaylistViewModel = viewModel()

    val scrollState = rememberScrollState()

    Column(modifier=Modifier.verticalScroll(scrollState)) {
        if (recPlaylistViewModel.songList.isNotEmpty()) {
            recPlaylistViewModel.songList.forEach {
                RecPlaylistItem(it)
            }
        }
        else {
            Text (
                text = "Loading..."
            )
        }
    }

}
