package dev.olog.msc.utils.k.extension

import dev.olog.msc.BuildConfig

fun Throwable.printStackTraceOnDebug(){
    if (BuildConfig.DEBUG){
        this.printStackTrace()
    }
}