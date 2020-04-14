package dev.olog.navigation

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dev.olog.core.isMarshmallow
import dev.olog.navigation.Navigator.Companion.REQUEST_CODE_HOVER_PERMISSION
import dev.olog.navigation.screens.Services
import javax.inject.Inject

internal class ServiceNavigatorImpl @Inject constructor(
    private val intents: Map<Services, @JvmSuppressWildcards Class<out Service>>
) : BaseNavigator(), ServiceNavigator {

    override fun toFloating(activity: FragmentActivity) {
        val clazz = intents[Services.FLOATING]
        mandatory(activity, clazz != null) ?: return
        val intent = Intent(activity, clazz)

        if (hasOverlayPermission(activity)){
            ContextCompat.startForegroundService(activity, intent)
        } else {
            val permissionIntent = createIntentToRequestOverlayPermission(activity)
            activity.startActivityForResult(permissionIntent, REQUEST_CODE_HOVER_PERMISSION)
        }
    }

    @CheckResult
    private fun hasOverlayPermission(context: Context): Boolean {
        return if (isMarshmallow()) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    @SuppressLint("InlinedApi")
    @CheckResult
    private fun createIntentToRequestOverlayPermission(context: Context): Intent {
        return Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
    }


}