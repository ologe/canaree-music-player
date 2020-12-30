package dev.olog.navigation.internal

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import dev.olog.navigation.Navigator
import dev.olog.navigation.ServiceNavigator
import dev.olog.navigation.destination.NavigationIntent
import dev.olog.navigation.destination.NavigationIntents
import javax.inject.Inject

internal class ServiceNavigatorImpl @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val intents: NavigationIntents,
) : ServiceNavigator {

    @SuppressLint("NewApi")
    override fun toFloatingWindow() {
        val activity = activityProvider() ?: return

        if (hasOverlayPermission(activity)){
            val intent = intents[NavigationIntent.FLOATING_SERVICE]?.get() ?: return
            ContextCompat.startForegroundService(activity, intent)
            return
        }
        val intent = createIntentToRequestOverlayPermission(activity)
        activity.startActivityForResult(intent, Navigator.HOVER_CODE)
    }

    override fun toMusicPlayFromUri(uri: Uri?) {
        uri ?: return
        val activity = activityProvider() ?: return
        val intent = intents[NavigationIntent.MUSIC_SERVICE_PLAY_URI]?.get() ?: return
        intent.data = uri
        ContextCompat.startForegroundService(activity, intent)
    }

    override fun toMusicPlayFromSearch() {
        val activity = activityProvider() ?: return
        val intent = intents[NavigationIntent.MUSIC_SERVICE_PLAY_FROM_SEARCH]?.get() ?: return
        ContextCompat.startForegroundService(activity, intent)
    }

    private fun hasOverlayPermission(context: Context): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Runtime permissions are required. Check for the draw overlay permission.
            Settings.canDrawOverlays(context)
        } else {
            // No runtime permissions required. We're all good.
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createIntentToRequestOverlayPermission(context: Context): Intent {
        return Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
    }

}