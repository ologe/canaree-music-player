package dev.olog.service.music.notification

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import dev.olog.service.music.MusicService
import dev.olog.service.music.R
import dev.olog.shared.MusicConstants
import dev.olog.shared.extensions.asServicePendingIntent

object NotificationActions {

    fun favorite(context: Context, isFavorite: Boolean): NotificationCompat.Action {
        val icon = if (isFavorite) R.drawable.vd_favorite else R.drawable.vd_not_favorite
        return NotificationCompat.Action.Builder(
            icon,
            "Toggle favorite",
            buildPendingIntent(context, MusicConstants.ACTION_TOGGLE_FAVORITE)
        ).build()
    }

    fun skipPrevious(context: Context, isPodcast: Boolean): NotificationCompat.Action {
        if (isPodcast){
            return podcastSkipPrevious(context)
        }
        return trackSkipPrevious(context)
    }


    fun playPause(context: Context, isPlaying: Boolean): NotificationCompat.Action {
        val icon = if (isPlaying) R.drawable.vd_pause_big else R.drawable.vd_play_big
        return NotificationCompat.Action.Builder(
            icon,
            "Toggle favorite",
            buildMediaPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE)
        ).build()
    }

    fun skipNext(context: Context, isPodcast: Boolean): NotificationCompat.Action {
        if (isPodcast){
            return podcastSkipNext(context)
        }
        return trackSkipNext(context)
    }

    private fun trackSkipPrevious(context: Context): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.vd_skip_previous,
            "Skip to previous",
            buildMediaPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        ).build()
    }

    private fun podcastSkipPrevious(context: Context): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.vd_replay,
            "Replay 10 seconds",
            buildPendingIntent(context, MusicConstants.ACTION_REPLAY_10_SECONDS)
        ).build()
    }

    private fun trackSkipNext(context: Context): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.vd_skip_next,
            "Skip to next",
            buildMediaPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
        ).build()
    }

    private fun podcastSkipNext(context: Context): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.vd_forward_30,
            "Forward 30 seconds",
            buildPendingIntent(context, MusicConstants.ACTION_FORWARD_30_SECONDS)
        ).build()
    }

    private fun buildPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, MusicService::class.java)
        intent.action = action
        return intent.asServicePendingIntent(context)
    }

    fun buildMediaPendingIntent(context: Context, action: Long): PendingIntent {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            ComponentName(context, MediaButtonReceiver::class.java),
            action
        )
    }

}