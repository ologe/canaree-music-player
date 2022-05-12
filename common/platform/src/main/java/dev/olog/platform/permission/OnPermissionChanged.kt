package dev.olog.platform.permission

interface OnPermissionChanged {
    fun onPermissionGranted(permission: Permission)
}

enum class Permission {
    STORAGE
}