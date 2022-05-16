package dev.olog.platform.permission

interface OnPermissionChanged {
    fun onPermissionGranted(permission: Permission)
}

