package dev.olog.presentation.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.activity_splash.SplashActivity
import dev.olog.presentation.fragment_queue.PlayingQueueFragment
import dev.olog.presentation.navigation.NavigatorModule

@Module
abstract class AndroidBindingModule {

    @ContributesAndroidInjector(modules = arrayOf(NavigatorModule::class))
    @PerActivity
    abstract fun splashActivity(): SplashActivity

    @ContributesAndroidInjector
    @PerFragment
    abstract fun playingQueueFragment() : PlayingQueueFragment

}
