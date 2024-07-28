package com.example.chordinate.recplaylist

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.CreationExtras
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.mapping.view.LocationDisplay
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay
import com.example.chordinate.RequestPermissions
import com.example.chordinate.checkPermissions
import kotlinx.coroutines.launch

@Composable
fun RecPlaylistScreen(locationDisplay: LocationDisplay) {
    val recPlaylistViewModel: RecPlaylistViewModel = viewModel()
    recPlaylistViewModel.setLocationDisplay(locationDisplay)

    val scrollState = rememberScrollState()

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                recPlaylistViewModel.refreshLocalPlaylist()
            }
            Lifecycle.Event.ON_RESUME -> {
                recPlaylistViewModel.refreshLocalPlaylist()
            }
            else -> {}
        }
    }

    Column(modifier=Modifier.verticalScroll(scrollState)) {
        if (recPlaylistViewModel.songList.isNotEmpty()) {
            recPlaylistViewModel.songList.forEach {
                RecPlaylistItem(it)
            }
        }
        else {
            Text (
                text = recPlaylistViewModel.loadStatus.value
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