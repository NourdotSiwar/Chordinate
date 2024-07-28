import android.R.attr.action
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


private const val TAG = "MyBroadcastReceiver"

class MyBroadcastReceiver : BroadcastReceiver() {
    object BroadcastTypes {
        const val SPOTIFY_PACKAGE: String = "com.spotify.music"
        const val PLAYBACK_STATE_CHANGED: String = SPOTIFY_PACKAGE + ".playbackstatechanged"
        const val QUEUE_CHANGED: String = SPOTIFY_PACKAGE + ".queuechanged"
        const val METADATA_CHANGED: String = SPOTIFY_PACKAGE + ".metadatachanged"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Spotify broadcast", intent.toString())
        if (intent.action.equals(BroadcastTypes.METADATA_CHANGED)) {
            val trackId = intent.getStringExtra("id")
            val artistName = intent.getStringExtra("artist")
            val albumName = intent.getStringExtra("album")
            val trackName = intent.getStringExtra("track")
            val trackLengthInSec = intent.getIntExtra("length", 0)

            Log.d("Spotify Broadcast", "Metadata Change - $trackName, $artistName, $albumName")
            // Do something with extracted information...
        } else if (intent.action.equals(BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            val trackId = intent.getStringExtra("id")
            val artistName = intent.getStringExtra("artist")
            val albumName = intent.getStringExtra("album")
            val trackName = intent.getStringExtra("track")
            val trackLengthInSec = intent.getIntExtra("length", 0)

            Log.d("Spotify Broadcast", "Playback state changed - $trackName, $artistName, $albumName")
        } else if (intent.action.equals(BroadcastTypes.QUEUE_CHANGED)) {
            // Sent only as a notification, your app may want to respond accordingly.
            Log.d("Spotify Broadcast", "Queue changed")
        }
    }
}