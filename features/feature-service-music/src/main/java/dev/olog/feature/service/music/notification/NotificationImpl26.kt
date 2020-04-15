package dev.olog.feature.service.music.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import dev.olog.domain.schedulers.Schedulers
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.feature.service.music.R
import dev.olog.feature.service.music.interfaces.INotification
import dev.olog.navigation.screens.ActivitiesMap
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
internal class NotificationImpl26 @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    service: Service,
    mediaSession: MediaSessionCompat,
    schedulers: Schedulers,
    activities: ActivitiesMap
) : NotificationImpl24(lifecycle, service, mediaSession, schedulers, activities) {

    override fun extendInitialization() {
        builder.setColorized(true)

        val nowPlayingChannelExists =
            notificationManager.getNotificationChannel(INotification.CHANNEL_ID) != null

        if (!nowPlayingChannelExists) {
            createChannel()
        }
    }

    private fun createChannel() {
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