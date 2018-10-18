package dev.olog.msc.presentation.playing.queue.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.base.FragmentXKey
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment


@Module(subcomponents = arrayOf(PlayingQueueFragmentSubComponent::class))
abstract class PlayingQueueFragmentInjector {

    @Binds
    @IntoMap
    @FragmentXKey(PlayingQueueFragment::class)
    internal abstract fun injectorFactory(builder: PlayingQueueFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
