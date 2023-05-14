package dev.olog.platform.permission

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
    @ApplicationContext private val context: Context
) {

    companion object {
        private val internalMandatoryPermissions = arrayOf(
            Permission.Storage,
        )
        val MandatoryPermissions = internalMandatoryPermissions
            .mapNotNull { it.manifest }
            .toTypedArray()
    }

    fun hasMandatoryPermissions(): Boolean {
        return hasPermissions(*internalMandatoryPermissions)
    }

    fun hasUserDisabledMandatoryPermissions(fragment: Fragment): Boolean {
        return hasUserDisabledPermissions(fragment, *internalMandatoryPermissions)
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

    fun hasPermissions(vararg permission: Permission): Boolean {
        return permission
            .mapNotNull { it.manifest }
            .all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
    }

    private fun hasUserDisabledPermissions(fragment: Fragment, vararg permission: Permission): Boolean {
        return permission
            .mapNotNull { it.manifest }
            .any {
                fragment.shouldShowRequestPermissionRationale(it)
            }
    }

}