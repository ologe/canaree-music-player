package dev.olog.msc

import android.content.Intent
import android.net.Uri
import dev.olog.msc.app.app
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