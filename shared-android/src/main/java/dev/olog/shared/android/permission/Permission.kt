package dev.olog.shared.android.permission

import android.Manifest
import dev.olog.shared.android.utils.isQ
import dev.olog.shared.android.utils.isTiramisu

enum class Permission(val manifest: String?) {
    Storage(
        when {
            isTiramisu() -> Manifest.permission.READ_MEDIA_AUDIO
            isQ() -> Manifest.permission.READ_EXTERNAL_STORAGE
            else -> Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    ),
    Notification(
        when {
            isTiramisu() -> Manifest.permission.POST_NOTIFICATIONS
            else -> null
        }
    )
}