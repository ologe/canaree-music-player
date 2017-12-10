package dev.olog.presentation.fragment_mini_queue.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.fragment_mini_queue.MiniQueueFragment

@Module(subcomponents = arrayOf(MiniQueueFragmentSubComponent::class))
abstract class MiniQueueFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(MiniQueueFragment::class)
    internal abstract fun injectorFactory(builder: MiniQueueFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
