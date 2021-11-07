package dev.olog.shared.android.permission

interface OnPermissionChanged {
    fun onPermissionGranted(permission: Permission)
}

