//package dev.olog.msc.pro
//
//import android.content.Context
//import android.content.pm.ApplicationInfo
//import android.content.pm.PackageManager
//import android.util.Base64
//import dev.olog.msc.dagger.qualifier.ApplicationContext
//import io.reactivex.Completable
//import io.reactivex.schedulers.Schedulers
//import java.security.MessageDigest
//import javax.inject.Inject
//
//private const val SIGNATURE = "zf1lNrVXNz+owbNEYH3bN9kSPO0="
//
//class Validation @Inject constructor(
//        @ApplicationContext private val context: Context
//
//){
//
//    private val packageManager = context.packageManager
//    private val packageName = context.packageName
//
//    fun isValid(): Completable {
//        return Completable.fromCallable{
//            checkSignature()
//            checkDebuggable()
//        }.subscribeOn(Schedulers.io())
//    }
//
//    private fun checkSignature() {
//        val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//        val messageDigest = MessageDigest.getInstance("SHA")
//        for (signature in packageInfo.signatures) {
//            messageDigest.reset()
//            messageDigest.update(signature.toByteArray())
//            val currentSignature = Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT)
//            if (SIGNATURE == currentSignature){
//                return
//            }
//        }
//        throw RuntimeException("invalid signature")
//    }
//
//    private fun checkDebuggable() {
//        val isDebuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE == 0
//        if (isDebuggable){
//            throw RuntimeException("must not be debuggable")
//        }
//    }
//
//}