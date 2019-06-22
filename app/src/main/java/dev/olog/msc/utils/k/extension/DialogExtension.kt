package dev.olog.msc.utils.k.extension

import android.app.AlertDialog
import android.view.WindowManager
import dev.olog.shared.isOreo

@Suppress("DEPRECATION")
fun AlertDialog.enableForService(){
    val windowType = if (isOreo())
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    window?.setType(windowType)
}