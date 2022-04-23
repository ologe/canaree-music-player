package dev.olog.shared.extension

import android.content.Intent
import android.content.pm.PackageManager

fun PackageManager.isIntentSafe(intent: Intent): Boolean {
    return queryIntentActivities(intent, 0).isNotEmpty()
}