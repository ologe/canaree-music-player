package dev.olog.msc.pro

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Base64
import dev.olog.msc.dagger.qualifier.ApplicationContext
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.security.MessageDigest
import javax.inject.Inject

private const val SIGNATURE = ""// todo find signature
                                // todo check if proguard hide it

private const val PLAY_STORE_ID = "com.android.vending"

class Validation @Inject constructor(
        @ApplicationContext private val context: Context

){

    private val packageManager = context.packageManager
    private val packageName = context.packageName

    fun isValid(): Single<Boolean> {
        return Single.fromCallable {
            checkSignature() && verifyInstaller() && !checkDebuggable()
        }.subscribeOn(Schedulers.io())
    }

    private fun checkSignature(): Boolean {
        val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        val messageDigest = MessageDigest.getInstance("SHA")
        for (signature in packageInfo.signatures) {
            messageDigest.reset()
            messageDigest.update(signature.toByteArray())
            val currentSignature = Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT)
//            println("this is the current signature $currentSignature") todo copy to signature
            if (SIGNATURE == currentSignature){
                return true
            }
        }
        return false
    }

    private fun verifyInstaller() : Boolean {
        val installer = packageManager.getInstallerPackageName(packageName)
        return !installer.isNullOrEmpty() && installer.startsWith(PLAY_STORE_ID)
    }

    private fun checkDebuggable() : Boolean {
        return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

}