package dev.olog.msc.floating.window.service.notification

import android.annotation.TargetApi
import android.app.*
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.floating.window.service.FloatingWindowService
import dev.olog.msc.utils.isOreo
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val CHANNEL_ID = "0xfff"

class FloatingWindowNotification @Inject constructor(
        private val service: Service,
        @ServiceLifecycle lifecycle: Lifecycle,
        private val notificationManager: NotificationManager,
        private val musicPreferencesUseCase: MusicPreferencesUseCase

) : DefaultLifecycleObserver {

    companion object {
        const val NOTIFICATION_ID = 0xABC
    }

    private val builder = NotificationCompat.Builder(service, CHANNEL_ID)
    private var disposable : Disposable? = null

    private var notificationTitle = ""

    init {
        lifecycle.addObserver(this)

    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

    fun startObserving() {
        disposable = musicPreferencesUseCase.observeLastMetadata()
                .filter { it.contains("|") }
                .subscribe({
                    notificationTitle = it
                    val notification = builder.setContentTitle(notificationTitle).build()
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }, Throwable::printStackTrace)
    }

    fun buildNotification(): Notification {
        if (isOreo()){
            createChannel()
        }

        return builder
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.vd_bird_singing)
                .setContentTitle(notificationTitle)
                .setContentText(service.getString(R.string.floating_window_notification_content_text))
                .setColor(0xff1f86ef.toInt())
                .setContentIntent(createContentIntent())
                .build()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel(){
        // create notification channel
        val name = service.getString(R.string.floating_window_notification_channel_title)
        val description = service.getString(R.string.floating_window_notification_channel_description)

        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    private fun createContentIntent() : PendingIntent {
        val intent = Intent(service, FloatingWindowService::class.java)
        intent.action = FloatingWindowService.ACTION_STOP

        return PendingIntent.getService(service, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}