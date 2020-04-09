package dev.olog.lib.offline.lyrics

import android.view.WindowManager
import androidx.appcompat.app.AlertDialog

@Suppress("DEPRECATION")
internal fun AlertDialog.enableForService(){
    val windowType = if (dev.olog.core.isOreo())
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    window?.setType(windowType)
}