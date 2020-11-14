package dev.olog.shared.android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object Permissions {

    private const val READ_CODE = 100

    private const val READ_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

    fun checkWriteCode(code: Int): Boolean {
        return code == READ_CODE
    }

    fun canReadStorage(context: Context): Boolean {
        return hasPermission(
            context,
            READ_STORAGE
        )
    }

    fun requestReadStorage(fragment: Fragment) {
        requestPermissions(
            fragment,
            READ_STORAGE,
            READ_CODE
        )
    }

    fun hasUserDisabledReadStorage(fragment: Fragment): Boolean {
        return hasUserDisabledPermission(
            fragment,
            READ_STORAGE
        )
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(fragment: Fragment, permission: String, requestCode: Int) {
        fragment.requestPermissions(arrayOf(permission), requestCode)
    }

    private fun hasUserDisabledPermission(fragment: Fragment, permission: String): Boolean {
        return !fragment.shouldShowRequestPermissionRationale(permission)
    }

}