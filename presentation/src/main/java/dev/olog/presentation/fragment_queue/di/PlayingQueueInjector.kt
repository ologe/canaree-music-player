package dev.olog.presentation.fragment_queue.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.fragment_queue.PlayingQueueFragment

@Module(subcomponents = arrayOf(PlayingQueueSubComponent::class))
abstract class PlayingQueueInjector {

    @Binds
    @IntoMap
    @FragmentKey(PlayingQueueFragment::class)
    internal abstract fun injectorFactory(builder: PlayingQueueSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
