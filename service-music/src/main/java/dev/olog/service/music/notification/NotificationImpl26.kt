package dev.olog.service.music.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dev.olog.core.PendingIntentFactory
import dev.olog.service.music.R
import dev.olog.service.music.interfaces.INotification
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
internal class NotificationImpl26 @Inject constructor(
    service: Service,
    mediaSession: MediaSessionCompat,
    pendingIntentFactory: PendingIntentFactory,
    notificationActions: NotificationActions,
) : NotificationImpl24(
    service = service,
    mediaSession = mediaSession,
    pendingIntentFactory = pendingIntentFactory,
    notificationActions = notificationActions
) {

    override fun extendInitialization() {
        builder.setColorized(true)

        val nowPlayingChannelExists = notificationManager.getNotificationChannel(INotification.CHANNEL_ID) != null

        if (!nowPlayingChannelExists){
            createChannel()
        }
    }

    private fun createChannel(){
        // create notification channel
        val name = service.getString(R.string.music_channel_id_notification)
        val description = service.getString(R.string.music_channel_id_notification_description)

        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(INotification.CHANNEL_ID, name, importance)
        channel.description = description
        channel.setShowBadge(false)
        channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }
}