package dev.olog.presentation.queue.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.feature.presentation.base.dagger.ScreenScope
import dev.olog.presentation.queue.PlayingQueueFragment

@Subcomponent(modules = [PlayingQueueFragmentModule::class])
@ScreenScope
interface PlayingQueueFragmentSubComponent : AndroidInjector<PlayingQueueFragment> {

    @Subcomponent.Factory
    interface Builder : AndroidInjector.Factory<PlayingQueueFragment>

}