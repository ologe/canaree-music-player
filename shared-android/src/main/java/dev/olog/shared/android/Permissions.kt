package dev.olog.shared.android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine

object Permissions {

    private const val READ_CODE = 100

    // TODO double check this and manifest if versions are correct
    private val READ_STORAGE = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> Manifest.permission.READ_MEDIA_AUDIO
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Manifest.permission.READ_EXTERNAL_STORAGE
        else -> Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    suspend fun awaitStorage(context: Context) {
        while (!canReadStorage(context)) {
            delay(50)
            continue
        }
    }

    @JvmStatic
    fun checkWriteCode(code: Int): Boolean {
        return code == READ_CODE
    }

    @JvmStatic
    fun canReadStorage(context: Context): Boolean {
        return hasPermission(
            context,
            READ_STORAGE
        )
    }

    @JvmStatic
    fun requestReadStorage(fragment: Fragment) {
        requestPermissions(
            fragment,
            READ_STORAGE,
            READ_CODE
        )
    }

    @JvmStatic
    fun hasUserDisabledReadStorage(fragment: Fragment): Boolean {
        return hasUserDisabledPermission(
            fragment,
            READ_STORAGE
        )
    }

    @JvmStatic
    private fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    private fun requestPermissions(fragment: Fragment, permission: String, requestCode: Int) {
        fragment.requestPermissions(arrayOf(permission), requestCode)
    }

    @JvmStatic
    private fun hasUserDisabledPermission(fragment: Fragment, permission: String): Boolean {
        return !fragment.shouldShowRequestPermissionRationale(permission)
    }

}