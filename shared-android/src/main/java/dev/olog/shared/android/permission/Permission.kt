package dev.olog.shared.android.permission

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import dev.olog.shared.android.utils.isQ

enum class Permission(val manifest: String) {
    Storage(if (isQ()) READ_EXTERNAL_STORAGE else WRITE_EXTERNAL_STORAGE)
}