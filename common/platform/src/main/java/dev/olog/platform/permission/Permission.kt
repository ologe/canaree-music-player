package dev.olog.platform.permission

import android.Manifest
import dev.olog.platform.isQ
import dev.olog.platform.isTiramisu

enum class Permission(val manifest: String?) {
    Storage(
        when {
            isTiramisu() -> Manifest.permission.READ_MEDIA_AUDIO
            isQ() -> Manifest.permission.READ_EXTERNAL_STORAGE
            else -> Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    ),
}