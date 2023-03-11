package dev.olog.platform.permission

import android.Manifest
import dev.olog.platform.BuildVersion

enum class Permission(val manifest: String?) {
    Storage(
        when {
            BuildVersion.isTiramisu() -> Manifest.permission.READ_MEDIA_AUDIO
            BuildVersion.isQ() -> Manifest.permission.READ_EXTERNAL_STORAGE
            else -> Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    ),
}