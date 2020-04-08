package dev.olog.feature.presentation.base.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object PlayStoreUtils {

    @JvmStatic
    fun open(activity: Activity){
        val uri = Uri.parse("market://details?id=${activity.packageName}")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        // TODO check flags
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            // TODO localization
            // TODO snackbar
            Toast.makeText(activity, "Play Store not found", Toast.LENGTH_SHORT)
                .show()
        }
    }

}