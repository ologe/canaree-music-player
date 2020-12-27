package dev.olog.shared.android

interface OnPermissionChanged {
    fun onPermissionGranted(permission: Permission)
}

enum class Permission {
    STORAGE
}