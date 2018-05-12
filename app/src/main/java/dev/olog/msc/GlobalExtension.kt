package dev.olog.msc

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import dev.olog.msc.app.app
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.utils.assertMainThread
import java.io.File

fun catchNothing(func:() -> Unit){
    try {
        func()
    } catch (ex: Exception){}
}

fun notifyItemChanged(path: String){
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.data = Uri.fromFile(File((path)))
    app.sendBroadcast(intent)
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