package com.example.chordinate.screens.recPlaylist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.chordinate.recplaylist.RecPlaylistViewModel
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun RecPlaylistScreen(fusedLocationClient: FusedLocationProviderClient) {
    val recPlaylistViewModel: RecPlaylistViewModel = viewModel()
    recPlaylistViewModel.setFusedLocationProvider(fusedLocationClient)

    val scrollState = rememberScrollState()

    OnLifecycleEvent { owner, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                recPlaylistViewModel.refreshLocalPlaylist()
            }
            else -> {}
        }
    }

    Column(modifier=Modifier.verticalScroll(scrollState)) {
        if (recPlaylistViewModel.loadStatus.value == RecPlaylistViewModel.PlaylistLoadStatus.LOADED) {
            recPlaylistViewModel.songList.forEach {
                RecPlaylistItem(it)
            }
        }
        else {

            Text (
                text = recPlaylistViewModel.getLoadMessage()
            )
        }
    }

}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}