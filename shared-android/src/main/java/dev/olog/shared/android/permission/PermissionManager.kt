package dev.olog.shared.android.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import javax.inject.Inject

class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private val mandatoryPermissions = arrayOf(
            Permission.Storage,
            Permission.Notification,
        )

        private const val MANDATORY_PERMISSION_CODE = 100
    }

    suspend fun awaitPermissions(
        vararg permission: Permission
    ) = coroutineScope {
        while (isActive) {
            if (hasPermissions(*permission)) {
                return@coroutineScope
            }
            delay(100)
        }
    }

    fun hasMandatoryPermissions(): Boolean {
        return hasPermissions(*mandatoryPermissions)
    }

    fun hasPermissions(vararg permission: Permission): Boolean {
        return permission
            .mapNotNull { it.manifest }
            .all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
    }

    fun requestMandatoryPermission(fragment: Fragment) {
        requestPermissions(
            fragment,
            MANDATORY_PERMISSION_CODE,
            *mandatoryPermissions,
        )
    }

    fun requestPermissions(
        fragment: Fragment,
        requestCode: Int,
        vararg permission: Permission
    ) {
        fragment.requestPermissions(permission.map { it.manifest }.toTypedArray(), requestCode)
    }

    fun isMandatoryPermissionsRequestCode(requestCode: Int): Boolean {
        return requestCode == MANDATORY_PERMISSION_CODE
    }

    fun hasUserDisabledMandatoryPermissions(fragment: Fragment): Boolean {
        return hasUserDisabledPermissions(fragment, *mandatoryPermissions)
    }

    fun hasUserDisabledPermissions(fragment: Fragment, vararg permission: Permission): Boolean {
        return permission
            .mapNotNull { it.manifest }
            .any {
                !fragment.shouldShowRequestPermissionRationale(it)
            }
    }

}