package dev.olog.platform

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import dev.olog.platform.extension.toast

object PlayStoreUtils {

    fun open(activity: Activity){
        val uri = Uri.parse("market://details?id=dev.olog.msc")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            activity.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            activity.toast("Play Store not found")
        }
    }

}