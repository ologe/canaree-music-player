package dev.olog.feature.base.permission

interface OnPermissionChanged {
    fun onPermissionGranted(permission: Permission)
}

