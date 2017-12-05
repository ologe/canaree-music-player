package dev.olog.music_service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.media.session.MediaSessionCompat
import dagger.Lazy
import dev.olog.music_service.R
import dev.olog.music_service.di.PerService
import dev.olog.music_service.interfaces.ActivityClass
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@PerService
class NotificationImpl26 @Inject constructor(
        service: Service,
        activityClass: ActivityClass,
        token: MediaSessionCompat.Token,
        notificationManager: Lazy<NotificationManager>
) : NotificationImpl24(service, activityClass, token, notificationManager) {

    companion object {
        private val CHANNEL_ID = "0x6d7363"
    }

    override fun extendInitialization() {
       createChannel()
    }

    private fun createChannel(){
        // create notification channel
        val name = service.getString(R.string.channel_id_notification)
        val description = service.getString(R.string.channel_id_notification_description)

        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        channel.setShowBadge(false)
        channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        notificationManager.get().createNotificationChannel(channel)
    }

    override fun provideNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(service, NotificationImpl26.CHANNEL_ID)
    }
}