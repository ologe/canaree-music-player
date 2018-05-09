package dev.olog.msc.utils.media.store

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

fun notifyMediaStoreItemChanged(context: Context, path: String){
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.data = Uri.fromFile(File((path)))
    context.sendBroadcast(intent)
}