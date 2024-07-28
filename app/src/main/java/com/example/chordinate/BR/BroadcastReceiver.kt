import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val TAG = "Spotify Broadcast"

class MyBroadcastReceiver : BroadcastReceiver() {


    object BroadcastTypes {
        private const val SPOTIFY_PACKAGE: String = "com.spotify.music"
        const val PLAYBACK_STATE_CHANGED: String = "$SPOTIFY_PACKAGE.playbackstatechanged"
        const val QUEUE_CHANGED: String = "$SPOTIFY_PACKAGE.queuechanged"
        const val METADATA_CHANGED: String = "$SPOTIFY_PACKAGE.metadatachanged"
    }

    object SharedData {
        var artistName: String? = null
        var trackId: String? = null
        var albumName: String? = null
        var trackName: String? = null
        // Other data fields as needed
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Spotify broadcast", intent.toString())
        if (intent.action.equals(BroadcastTypes.METADATA_CHANGED)) {
            SharedData.artistName = intent.getStringExtra("artist")
            SharedData.trackId = intent.getStringExtra("id")
            SharedData.albumName = intent.getStringExtra("album")
            SharedData.trackName = intent.getStringExtra("track")

            Log.d(TAG, "Metadata Change: ${SharedData.artistName} made ${SharedData.trackName}")
        } else if (intent.action.equals(BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            SharedData.artistName = intent.getStringExtra("artist")
            SharedData.trackId = intent.getStringExtra("id")
            SharedData.albumName = intent.getStringExtra("album")
            SharedData.trackName = intent.getStringExtra("track")
            Log.d(
                TAG,
                "Playback state Change: ${SharedData.artistName} made ${SharedData.trackName}"
            )

        } else if (intent.action.equals(BroadcastTypes.QUEUE_CHANGED)) {
            // Sent only as a notification, your app may want to respond accordingly.
            Log.d(TAG, "Queue changed")
        }
    }

}
