package dev.olog.platform

import android.app.Activity
import android.content.Intent
import android.net.Uri
import dev.olog.shared.extension.toast

object PlayStoreUtils {

    @JvmStatic
    fun open(activity: Activity){
        val uri = Uri.parse("market://details?id=${activity.packageName}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            activity.toast("Play Store not found")
        }
    }

}