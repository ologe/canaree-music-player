package dev.olog.msc.music.service.notification

import android.app.Notification

interface INotification {

    companion object {
        const val NOTIFICATION_ID : Int = 0x6d7363
        const val CHANNEL_ID = "$NOTIFICATION_ID"
        const val IMAGE_SIZE = 200
    }

    fun update(state: MusicNotificationState): Notification
    fun cancel()

}