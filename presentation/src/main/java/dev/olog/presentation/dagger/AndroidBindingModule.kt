package dev.olog.presentation.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.fragment_queue.PlayingQueueFragment

@Module
abstract class AndroidBindingModule {

    @ContributesAndroidInjector
    @PerFragment
    abstract fun playingQueueFragment() : PlayingQueueFragment

}
