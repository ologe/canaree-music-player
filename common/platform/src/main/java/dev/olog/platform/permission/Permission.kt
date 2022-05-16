package dev.olog.platform.permission

import android.Manifest
import dev.olog.shared.isQ

enum class Permission(val manifest: String) {
    Storage(if (isQ()) Manifest.permission.READ_EXTERNAL_STORAGE else Manifest.permission.WRITE_EXTERNAL_STORAGE)
}