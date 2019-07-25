package dev.olog.data.utils

import android.app.PendingIntent
import android.app.RecoverableSecurityException
import dev.olog.shared.android.utils.isQ

internal fun handleRecoverableSecurityException(action: () -> Unit) {
    if (!isQ()) {
        return action()
    }
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