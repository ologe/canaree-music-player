package dev.olog.msc.utils.k.extension

import android.support.v7.app.AlertDialog
import android.view.WindowManager
import dev.olog.msc.utils.isOreo

fun AlertDialog.enableForService(){
    val windowType = if (isOreo())
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    window.setType(windowType)
}