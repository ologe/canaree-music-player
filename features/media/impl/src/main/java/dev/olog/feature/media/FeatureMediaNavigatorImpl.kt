package dev.olog.feature.media

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.shared.extension.asServicePendingIntent
import javax.inject.Inject

class FeatureMediaNavigatorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FeatureMediaNavigator {

    override fun serviceComponent(): ComponentName {
        return ComponentName(context, MusicService::class.java)
    }

    override fun startService(action: MusicServiceAction, uri: Uri?) {
        startService(action.name, uri)
    }

    override fun startService(action: String, uri: Uri?) {
        val serviceIntent = Intent(context, MusicService::class.java)
        serviceIntent.action = action
        serviceIntent.data = uri
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    override fun pendingIntent(action: String): PendingIntent {
        val intent = Intent(context, MusicService::class.java)
        intent.action = action
        return intent.asServicePendingIntent(context, PendingIntent.FLAG_CANCEL_CURRENT)
    }
}