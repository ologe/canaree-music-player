package dev.olog.msc.presentation.playing.queue.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment


@Module(subcomponents = arrayOf(PlayingQueueFragmentSubComponent::class))
abstract class PlayingQueueFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(PlayingQueueFragment::class)
    internal abstract fun injectorFactory(builder: PlayingQueueFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
