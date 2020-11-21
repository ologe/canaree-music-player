package dev.olog.offlinelyrics

import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import dev.olog.shared.android.utils.isOreo

@Suppress("DEPRECATION")
fun AlertDialog.enableForService(){
    val windowType = if (isOreo())
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    window?.setType(windowType)
}