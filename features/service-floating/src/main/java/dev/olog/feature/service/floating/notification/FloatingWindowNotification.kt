package dev.olog.feature.service.floating.notification

import android.app.*
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.feature.service.floating.FloatingWindowService
import dev.olog.feature.service.floating.R
import dev.olog.shared.android.extensions.asServicePendingIntent
import dev.olog.core.isOreo
import dev.olog.shared.android.extensions.colorControlNormal
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val CHANNEL_ID = "0xfff"

class FloatingWindowNotification @Inject constructor(
    private val service: Service,
    @ServiceLifecycle private val lifecycle: Lifecycle,
    private val notificationManager: NotificationManager,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val schedulers: Schedulers

) : DefaultLifecycleObserver {

    companion object {
        const val NOTIFICATION_ID = 0xABC
    }

    private val builder = NotificationCompat.Builder(
        service,
        CHANNEL_ID
    )

    private var disposable by autoDisposeJob()

    private var notificationTitle = ""

    fun startObserving() {
        // keeps playing song in sync
        disposable = musicPreferencesUseCase.observeLastMetadata()
            .filter { it.isNotEmpty() }
            .onEach {
                notificationTitle = it.description
                val notification = builder.setContentTitle(notificationTitle).build()
                notificationManager.notify(NOTIFICATION_ID, notification)
            }.flowOn(schedulers.cpu)
            .launchIn(lifecycle.coroutineScope)
    }

    fun buildNotification(): Notification {
        createChannel()

        return builder
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.vd_notification_icon)
            .setContentTitle(notificationTitle)
            .setContentText(service.getString(R.string.floating_window_notification_content_text))
            .setColor(service.colorControlNormal())
            .setContentIntent(createContentIntent())
            .setGroup("dev.olog.msc.FLOATING")
            .build()
    }

    private fun createChannel() {
        if (!isOreo()) {
            return
        }
        val nowPlayingChannelExists = notificationManager.getNotificationChannel(CHANNEL_ID) != null
        if (nowPlayingChannelExists) {
            return
        }

        // create notification channel
        val name = service.getString(R.string.floating_window_notification_channel_title)
        val description =
            service.getString(R.string.floating_window_notification_channel_description)

        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    private fun createContentIntent(): PendingIntent {
        val intent = Intent(service, FloatingWindowService::class.java)
        intent.action = FloatingWindowService.ACTION_STOP
        return intent.asServicePendingIntent(service, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}