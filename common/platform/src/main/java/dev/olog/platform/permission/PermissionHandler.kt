package dev.olog.platform.permission

import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import dev.olog.platform.permission.PermissionResult.Denied
import dev.olog.platform.permission.PermissionResult.Granted
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PermissionHandler(
    private val fragment: Fragment,
    private val permission: Permission,
) {

    private val launcher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(RequestPermission()) {
            val result = if (it) Granted else Denied
            continuation?.resume(result)
        }

    private var continuation: CancellableContinuation<PermissionResult>? = null

    suspend fun request(): PermissionResult {
        if (checkSelfPermission(fragment.requireContext(), permission.manifest) == PERMISSION_GRANTED) {
            return Granted
        }
        if (fragment.shouldShowRequestPermissionRationale(permission.manifest)) {
            return PermissionResult.RequestRationale
        }
        this.continuation?.cancel()

        return suspendCancellableCoroutine {
            this.continuation = it
            launcher.launch(permission.manifest)
            it.invokeOnCancellation {
                this.continuation = null
            }
        }
    }

    fun dispose() {
        launcher.unregister()
    }

}