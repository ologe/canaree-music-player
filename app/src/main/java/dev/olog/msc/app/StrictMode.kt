package dev.olog.msc.app

import android.os.StrictMode
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.mini.player.MiniPlayerFragment
import dev.olog.msc.presentation.player.PlayerFragment
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.shared.isOreo

object StrictMode {

    fun initialize(){
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())

        vmPolicy()
    }

    /**
        Do not detect:
        [StrictMode.VmPolicy.Builder.detectCleartextNetwork] -> cause ANR
        [StrictMode.VmPolicy.Builder.detectUntaggedSockets] -> annoying message
     */
    private fun vmPolicy(){
        val vmBuilder = StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .detectFileUriExposure()
                .detectLeakedClosableObjects()
                .detectLeakedRegistrationObjects()
                .detectLeakedSqlLiteObjects()

        if (isOreo()){
            vmBuilder.detectContentUriWithoutPermission()
                    .penaltyDeathOnFileUriExposure()
        }

        vmBuilder.penaltyLog()
                .setClassInstanceLimit(MainActivity::class.java, 1)
                .setClassInstanceLimit(PlayerFragment::class.java, 1)
                .setClassInstanceLimit(MiniPlayerFragment::class.java, 1)
                .setClassInstanceLimit(SearchFragment::class.java, 1)

        StrictMode.setVmPolicy(vmBuilder.build())
    }

}