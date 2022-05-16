package dev.olog.platform.permission

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import javax.inject.Inject

class PermissionManager @Inject constructor() {

    fun requestPermissionHandler(fragment: Fragment, permission: Permission): PermissionHandler {
        return PermissionHandler(fragment, permission)
    }

    suspend fun awaitPermission(context: Context, permission: Permission) = coroutineScope {
        do {
            if (hasPermission(context, permission)) {
                return@coroutineScope
            }
            delay(50)
        } while (isActive)
    }

    fun hasPermission(context: Context, permission: Permission): Boolean {
        return checkSelfPermission(context, permission.manifest) == PERMISSION_GRANTED
    }

}