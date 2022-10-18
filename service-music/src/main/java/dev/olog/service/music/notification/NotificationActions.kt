package dev.olog.service.music.notification

import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import dev.olog.core.PendingIntentFactory
import dev.olog.intents.MusicServiceCustomAction
import dev.olog.service.music.MusicService
import dev.olog.service.music.R
import javax.inject.Inject

internal class NotificationActions @Inject constructor(
    private val service: Service,
    private val pendingIntentFactory: PendingIntentFactory,
) {

    fun favorite(isFavorite: Boolean): NotificationCompat.Action {
        val icon = if (isFavorite) R.drawable.vd_favorite else R.drawable.vd_not_favorite
        return NotificationCompat.Action.Builder(
            icon,
            "Toggle favorite",
            buildPendingIntent(MusicServiceCustomAction.TOGGLE_FAVORITE.name)
        ).build()
    }

    fun skipPrevious(isPodcast: Boolean): NotificationCompat.Action {
        if (isPodcast){
            return podcastSkipPrevious()
        }
        return trackSkipPrevious()
    }


    fun playPause(isPlaying: Boolean): NotificationCompat.Action {
        val icon = if (isPlaying) R.drawable.vd_pause_big else R.drawable.vd_play_big
        return NotificationCompat.Action.Builder(
            icon,
            "Toggle favorite",
            buildMediaPendingIntent(PlaybackStateCompat.ACTION_PLAY_PAUSE)
        ).build()
    }

    fun skipNext(isPodcast: Boolean): NotificationCompat.Action {
        if (isPodcast){
            return podcastSkipNext()
        }
        return trackSkipNext()
    }

    private fun trackSkipPrevious(): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.vd_skip_previous,
            "Skip to previous",
            buildMediaPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        ).build()
    }

    private fun podcastSkipPrevious(): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.vd_replay,
            "Replay 10 seconds",
            buildPendingIntent(MusicServiceCustomAction.REPLAY_10.name)
        ).build()
    }

    private fun trackSkipNext(): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.vd_skip_next,
            "Skip to next",
            buildMediaPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
        ).build()
    }

    private fun podcastSkipNext(): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.vd_forward_30,
            "Forward 30 seconds",
            buildPendingIntent(MusicServiceCustomAction.FORWARD_30.name)
        ).build()
    }

    private fun buildPendingIntent(action: String): PendingIntent {
        val intent = Intent(service, MusicService::class.java)
        intent.action = action
        return pendingIntentFactory.service(intent)
    }

    fun buildMediaPendingIntent(action: Long): PendingIntent {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
            service,
            ComponentName(service, MediaButtonReceiver::class.java),
            action
        )
    }

}