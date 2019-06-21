package dev.olog.msc

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import dev.olog.shared.assertMainThread
import java.io.File

fun catchNothing(func:() -> Unit){
    try {
        func()
    } catch (ex: Exception){
        ex.printStackTrace()
    }
}

fun notifyItemChanged(context: Context, path: String){
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.data = Uri.fromFile(File((path)))
    context.sendBroadcast(intent)
}

private var isLowMemory : Boolean? = null

fun isLowMemoryDevice(context: Context): Boolean {
    assertMainThread()
    if (isLowMemory == null){
        val manager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        isLowMemory = manager.isLowRamDevice
    }
    return isLowMemory!!
}