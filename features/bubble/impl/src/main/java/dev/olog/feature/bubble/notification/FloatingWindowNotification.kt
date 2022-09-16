package dev.olog.feature.bubble.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import dev.olog.core.ServiceScope
import dev.olog.core.gateway.PlayingGateway
import dev.olog.feature.bubble.FloatingWindowService
import dev.olog.feature.bubble.api.FloatingWindowsConstants
import dev.olog.shared.extension.asServicePendingIntent
import dev.olog.shared.extension.collectOnLifecycle
import dev.olog.shared.isOreo
import dev.olog.ui.colorControlNormal
import javax.inject.Inject

private const val CHANNEL_ID = "0xfff"

class FloatingWindowNotification @Inject constructor(
    private val service: Service,
    private val serviceScope: ServiceScope,
    private val notificationManager: NotificationManager,
    private val playingGateway: PlayingGateway,
) {

    companion object {
        const val NOTIFICATION_ID = 0xABC
    }

    private val builder = NotificationCompat.Builder(
        service,
        CHANNEL_ID
    )

    private var notificationTitle = ""

    init {
        startObserving()
    }

    private fun startObserving() {
        // keeps playing song in sync
        playingGateway.observe()
            .collectOnLifecycle(serviceScope) {
//                notificationTitle = it.description todo
//                val notification = builder.setContentTitle(notificationTitle).build()
//                notificationManager.notify(NOTIFICATION_ID, notification)
            }
    }

    fun buildNotification(): Notification {
        createChannel()

        return builder
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(dev.olog.ui.R.drawable.vd_bird_singing)
            .setContentTitle(notificationTitle)
            .setContentText(service.getString(localization.R.string.floating_window_notification_content_text))
            .setColor(service.colorControlNormal())
            .setContentIntent(createContentIntent())
            .setGroup("dev.olog.msc.FLOATING")
            .build()
    }

    private fun createChannel() {
        if (!isOreo()){
            return
        }
        val nowPlayingChannelExists = notificationManager.getNotificationChannel(CHANNEL_ID) != null
        if (nowPlayingChannelExists){
            return
        }

        // create notification channel
        val name = service.getString(localization.R.string.floating_window_notification_channel_title)
        val description =
            service.getString(localization.R.string.floating_window_notification_channel_description)

        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    private fun createContentIntent(): PendingIntent {
        val intent = Intent(service, FloatingWindowService::class.java)
        intent.action = FloatingWindowsConstants.ACTION_STOP
        return intent.asServicePendingIntent(service, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}