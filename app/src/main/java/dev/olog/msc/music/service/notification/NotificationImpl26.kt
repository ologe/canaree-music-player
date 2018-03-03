package dev.olog.msc.music.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.media.session.MediaSessionCompat
import dagger.Lazy
import dev.olog.msc.R
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class NotificationImpl26 @Inject constructor(
        service: Service,
        token: MediaSessionCompat.Token,
        notificationManager: Lazy<NotificationManager>

) : NotificationImpl24(service, token, notificationManager) {

    override fun extendInitialization() {
        builder.setColorized(true)

        createChannel()
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
        notificationManager.get().createNotificationChannel(channel)
    }
}