package dev.olog.feature.presentation.base.activity

interface OnPermissionChanged {
    fun onPermissionGranted(permission: Permission)
}

enum class Permission {
    STORAGE
}