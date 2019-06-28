package dev.olog.service.floating.notification

import android.annotation.TargetApi
import android.app.*
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.service.floating.FloatingWindowService
import dev.olog.service.floating.R
import dev.olog.shared.extensions.asServicePendingIntent
import dev.olog.shared.extensions.unsubscribe
import dev.olog.shared.utils.isOreo
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val CHANNEL_ID = "0xfff"

class FloatingWindowNotification @Inject constructor(
    private val service: Service,
    @ServiceLifecycle lifecycle: Lifecycle,
    private val notificationManager: NotificationManager,
    private val musicPreferencesUseCase: MusicPreferencesGateway

) : DefaultLifecycleObserver {

    companion object {
        const val NOTIFICATION_ID = 0xABC
    }

    private val builder = NotificationCompat.Builder(
        service,
        CHANNEL_ID
    )
    private var disposable: Disposable? = null

    private var notificationTitle = ""

    init {
        lifecycle.addObserver(this)

    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

    fun startObserving() {
        disposable = musicPreferencesUseCase.observeLastMetadata()
            .filter { it.isNotEmpty() }
            .subscribe({
                notificationTitle = it.description
                val notification = builder.setContentTitle(notificationTitle).build()
                notificationManager.notify(NOTIFICATION_ID, notification)
            }, Throwable::printStackTrace)
    }

    fun buildNotification(): Notification {
        if (isOreo()) {
            createChannel()
        }

        return builder
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.vd_bird_singing)
            .setContentTitle(notificationTitle)
            .setContentText(service.getString(R.string.floating_window_notification_content_text))
            .setColor(ContextCompat.getColor(service, R.color.dark_grey))
            .setContentIntent(createContentIntent())
            .build()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
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