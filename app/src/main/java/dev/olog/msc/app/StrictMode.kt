package dev.olog.msc.app

import android.os.StrictMode
import dev.olog.msc.BuildConfig
import javax.inject.Inject

class StrictMode @Inject constructor(){

    fun initialize(){
        if (BuildConfig.DEBUG){
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())

            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
        }
    }

}