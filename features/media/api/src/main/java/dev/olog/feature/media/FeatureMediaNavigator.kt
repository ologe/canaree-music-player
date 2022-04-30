package dev.olog.feature.media

import android.app.PendingIntent
import android.content.ComponentName
import android.net.Uri

interface FeatureMediaNavigator {

    fun serviceComponent(): ComponentName

    fun startService(action: MusicServiceAction, uri: Uri?)
    fun startService(action: String, uri: Uri?)

    fun pendingIntent(action: String): PendingIntent

}