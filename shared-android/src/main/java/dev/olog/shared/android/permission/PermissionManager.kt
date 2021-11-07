package dev.olog.shared.android.permission

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private val DELAY = Duration.milliseconds(100)
    }

    suspend fun awaitPermission(permission: Permission) {
        while (!hasPermission(permission)) {
            delay(DELAY)
        }
    }

    fun checkPermissionRequestCode(code: Int): Boolean = code == PERMISSION_REQUEST_CODE

    fun hasPermission(permission: Permission): Boolean {
        return checkSelfPermission(context, permission.manifest) == PERMISSION_GRANTED
    }

    fun requestPermissions(fragment: Fragment, permission: Permission) {
        fragment.requestPermissions(arrayOf(permission.manifest), PERMISSION_REQUEST_CODE)
    }

    fun hasUserDisabledPermission(fragment: Fragment, permission: Permission): Boolean {
        return !fragment.shouldShowRequestPermissionRationale(permission.manifest)
    }

}