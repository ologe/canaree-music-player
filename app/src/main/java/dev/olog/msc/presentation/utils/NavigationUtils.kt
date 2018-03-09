package dev.olog.msc.presentation.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import dev.olog.msc.utils.k.extension.toast

fun openPlayStore(activity: Activity){
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

fun openReportBugs(activity: Activity){
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("dev.eugeniu.olog@gmail.com"))
    intent.putExtra(Intent.EXTRA_SUBJECT, "Next bug report")
    activity.startActivity(intent)
}