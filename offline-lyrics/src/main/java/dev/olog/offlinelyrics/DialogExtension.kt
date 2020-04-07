package dev.olog.offlinelyrics

import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import dev.olog.core.isOreo

@Suppress("DEPRECATION")
internal fun AlertDialog.enableForService(){
    val windowType = if (dev.olog.core.isOreo())
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    window?.setType(windowType)
}