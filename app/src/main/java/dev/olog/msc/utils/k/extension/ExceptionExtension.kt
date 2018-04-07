package dev.olog.msc.utils.k.extension

import com.crashlytics.android.Crashlytics
import dev.olog.msc.BuildConfig

fun Throwable.printStackTraceOnDebug(){
    if (BuildConfig.DEBUG){
        this.printStackTrace()
    }
}

fun Throwable.logStackStace(){
    if (!BuildConfig.DEBUG){
        Crashlytics.logException(this)
    }
    this.printStackTrace()
}

fun crashlyticsLog(msg: String){
    if (!BuildConfig.DEBUG){
        Crashlytics.log(msg)
    }
}