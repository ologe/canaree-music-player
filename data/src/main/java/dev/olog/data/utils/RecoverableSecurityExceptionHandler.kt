package dev.olog.data.utils

import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.os.Build

internal fun handleRecoverableSecurityException(action: () -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        return action()
    }
    // handle android Q scoped storage
    try {
        action()
    } catch (ex: RecoverableSecurityException) {
        try {
            // TODO check if works
            ex.userAction.actionIntent.send()
        } catch (ignored: PendingIntent.CanceledException) {
        }
    }
}