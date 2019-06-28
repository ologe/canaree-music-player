package dev.olog.presentation.queue.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.queue.PlayingQueueFragment


@Module(subcomponents = arrayOf(PlayingQueueFragmentSubComponent::class))
abstract class PlayingQueueFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(PlayingQueueFragment::class)
    internal abstract fun injectorFactory(builder: PlayingQueueFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
