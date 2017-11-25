package dev.olog.msc

import android.os.StrictMode
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.data.DataConstants
import dev.olog.music_service.MusicService
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.fragment_detail.DetailFragment
import dev.olog.presentation.fragment_detail.DetailFragmentViewModel
import dev.olog.presentation.fragment_player.PlayerFragment
import dev.olog.presentation.fragment_player.PlayerFragmentViewModel
import dev.olog.presentation.fragment_tab.TabFragment
import dev.olog.presentation.fragment_tab.TabFragmentViewModel

class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        DataConstants.init(resources)


        if (BuildConfig.DEBUG) {
            initStrictMode()
            LeakCanary.install(this)
        }

    }

    private fun initStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .setClassInstanceLimit(MainActivity::class.java, 1)
                .setClassInstanceLimit(MusicService::class.java, 1)
                .setClassInstanceLimit(TabFragment::class.java, TabViewPagerAdapter.ITEM_COUNT)
                .setClassInstanceLimit(TabFragmentViewModel::class.java, 1)
                .setClassInstanceLimit(DetailFragment::class.java, 1)
                .setClassInstanceLimit(DetailFragmentViewModel::class.java, 1)
                .setClassInstanceLimit(PlayerFragment::class.java, 1)
                .setClassInstanceLimit(PlayerFragmentViewModel::class.java, 1)
                .build())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().create(this)
    }
}
