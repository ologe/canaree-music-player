package dev.olog.feature.bubble.impl

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.PendingIntentFactory
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.platform.extension.colorControlNormal
import dev.olog.platform.extension.lifecycleOwner
import dev.olog.platform.isOreo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CHANNEL_ID = "0xfff"

class FloatingWindowNotification @Inject constructor(
    private val service: Service,
    private val notificationManager: NotificationManager,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val pendingIntentFactory: PendingIntentFactory,
) : DefaultLifecycleObserver {

    companion object {
        const val NOTIFICATION_ID = 0xABC
    }

    private val builder = NotificationCompat.Builder(
        service,
        CHANNEL_ID
    )
    private var disposable: Job? = null

    private var notificationTitle = ""

    init {
        service.lifecycleOwner.lifecycle.addObserver(this)

    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable?.cancel()
    }

    fun startObserving() {
        disposable?.cancel()
        disposable = GlobalScope.launch {
            // keeps playing song in sync
            musicPreferencesUseCase.observeLastMetadata()
                .filter { it.isNotEmpty() }
                .collect {
                    notificationTitle = it.description
                    val notification = builder.setContentTitle(notificationTitle).build()
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }
        }
    }

    fun buildNotification(): Notification {
        createChannel()

        return builder
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.vd_bird_singing)
            .setContentTitle(notificationTitle)
            .setContentText(service.getString(R.string.floating_window_notification_content_text))
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
        return pendingIntentFactory.createForService(intent)
    }

}