package dev.olog.presentation.interfaces

interface OnPermissionChanged {
    fun onPermissionGranted(permission: Permission)
}

enum class Permission {
    STORAGE
}