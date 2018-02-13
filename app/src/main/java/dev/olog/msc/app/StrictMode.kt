package dev.olog.msc.app

import android.os.StrictMode
import dev.olog.msc.BuildConfig
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.mini.player.MiniPlayerFragment
import dev.olog.msc.presentation.player.PlayerFragment
import dev.olog.msc.presentation.search.SearchFragment
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
                    .setClassInstanceLimit(MainActivity::class.java, 1)
                    .setClassInstanceLimit(PlayerFragment::class.java, 1)
                    .setClassInstanceLimit(MiniPlayerFragment::class.java, 1)
                    .setClassInstanceLimit(SearchFragment::class.java, 1)
                    .build())
        }
    }

}