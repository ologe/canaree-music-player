package dev.olog.msc

import androidx.work.WorkManager

val workManager : WorkManager = WorkManager.getInstance()

fun catchNothing(func:() -> Unit){
    try {
        func()
    } catch (ex: Exception){}
}