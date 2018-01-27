package dev.olog.presentation.pro

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Context
import android.provider.Settings
import com.google.android.vending.licensing.*
import dev.olog.shared.ApplicationContext
import dev.olog.shared_android.interfaces.pro.ILicensing
import javax.inject.Inject

// salt, 20 bytes
private val SALT = byteArrayOf(
        -113, -11, 32, -64, 89, -45, 77, -117, -36, -46,
        65, 30, -128, -103, -57, 74, -64, 51, 88, -95
)

private const val DEVICE_ID = Settings.Secure.ANDROID_ID
private const val PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgjEHhgXIywB+vz6d7ljbp2bi3Fx9jQpG/Yg0q2BePJOYYd7pWA97fY1jyrt5l/+k//FeFJoovwunQeoVJmzefKBFSgnn/+JWz5diW0uFPm9l8GgU70lMboqQ9nVPz7t0gYa9p8JimRFd1rpCaHCs6LOcQ9Odg5YIjCJBudlFCH6e0TCFpdw3HuzUR+4jjOCB3lS3R4e8K4hXJqg4BbCM+gN9F0IbxnFep8/TSZFseSfMf3ZUp7PTP64N4wnlNuQ7MBkOBIrcl2hbPuYb5/QmnSicgVqBrISB5qX9AmHjc6eaSUjl153rg4m5ulW9L/NaYefwiWMBIQPzym6Y6g7x+QIDAQAB"

class Licensing @Inject internal constructor(
        @ApplicationContext context: Context

) : ILicensing, DefaultLifecycleObserver {

    private val obfuscator : Obfuscator = AESObfuscator(SALT, context.packageName, DEVICE_ID)
    private val policy: Policy = ServerManagedPolicy(context, obfuscator)
    private val licenseChecker = LicenseChecker(context, policy, PUBLIC_KEY)

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun check(listener: LicenseCheckerCallback) {
        licenseChecker.checkAccess(listener)
    }

    override fun onStop(owner: LifecycleOwner) {
        licenseChecker.onDestroy()
    }

}