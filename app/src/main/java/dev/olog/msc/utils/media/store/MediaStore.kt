package dev.olog.msc.utils.media.store

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import java.io.File

fun notifyMediaStore(context: Context, path: String){
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.data = Uri.fromFile(File((path)))
    context.sendBroadcast(intent)
}

fun notifySongMediaStore(context: Context){
    context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
}