package dev.olog.msc

import android.os.StrictMode
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.data.DataConstants
import dev.olog.floating_info.FloatingInfoService
import dev.olog.music_service.MusicService
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.fragment_mini_player.MiniPlayerFragment
import dev.olog.presentation.fragment_mini_player.MiniPlayerFragmentViewModel
import dev.olog.presentation.fragment_mini_queue.MiniQueueFragment
import dev.olog.presentation.fragment_mini_queue.MiniQueueViewModel
import dev.olog.presentation.fragment_player.PlayerFragment
import dev.olog.presentation.fragment_player.PlayerFragmentViewModel
import dev.olog.presentation.fragment_search.SearchFragment
import dev.olog.presentation.fragment_search.SearchFragmentViewModel
import dev.olog.presentation.fragment_tab.TabFragment
import dev.olog.presentation.fragment_tab.TabFragmentViewModel
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicServiceController
import javax.inject.Inject


class App : DaggerApplication() {

    @Inject lateinit var musicServiceController : MusicServiceController

    override fun onCreate() {
        super.onCreate()

        DataConstants.init(resources)


        if (BuildConfig.DEBUG) {
            initStrictMode()
//            LeakCanary.install(this)
            initRxJavaDebug()
        }

    }

    private fun initRxJavaDebug(){
//        RxJava2Debug.enableRxJava2AssemblyTracking(arrayOf(
//                "dev.olog.msc",
//                "dev.olog.data",
//                "dev.olog.domain",
//                "dev.olog.music_service",
//                "dev.olog.floating_info",
//                "dev.olog.presentation",
//                "dev.olog.shared"
//        ))
    }

    private fun initStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
//                .penaltyDeath()
                .setClassInstanceLimit(MainActivity::class.java, 1)
                .setClassInstanceLimit(TabViewPagerAdapter::class.java, 1)

                .setClassInstanceLimit(MusicService::class.java, 1)
                .setClassInstanceLimit(FloatingInfoService::class.java, 1)

                .setClassInstanceLimit(TabFragment::class.java, TabViewPagerAdapter.ITEM_COUNT)
                .setClassInstanceLimit(TabFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(PlayerFragment::class.java, 1)
                .setClassInstanceLimit(PlayerFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(MiniPlayerFragment::class.java, 1)
                .setClassInstanceLimit(MiniPlayerFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(MiniQueueFragment::class.java, 1)
                .setClassInstanceLimit(MiniQueueViewModel::class.java, 1)

                .setClassInstanceLimit(SearchFragment::class.java, 1)
                .setClassInstanceLimit(SearchFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(Navigator::class.java, 1)
                .build())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().create(this)
    }
}
