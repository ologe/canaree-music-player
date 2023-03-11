package dev.olog.feature.media.impl.navigator

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.media.api.FeatureMediaNavigator
import dev.olog.feature.media.impl.MusicService
import javax.inject.Inject

class FeatureMediaNavigatorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FeatureMediaNavigator {

    override fun createComponentName(): ComponentName {
        return ComponentName(context, MusicService::class.java)
    }

    override fun createIntent(action: String, data: Uri?): Intent {
        val intent = Intent(context, MusicService::class.java)
        intent.action = action
        intent.data = data
        return intent
    }

    override fun startService(action: String, data: Uri?) {
        val intent = createIntent(action, data)
        ContextCompat.startForegroundService(context, intent)
    }
}