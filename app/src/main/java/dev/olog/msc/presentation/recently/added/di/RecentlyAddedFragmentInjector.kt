package dev.olog.msc.presentation.recently.added.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment

@Module(subcomponents = arrayOf(RecentlyAddedFragmentSubComponent::class))
abstract class RecentlyAddedFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(RecentlyAddedFragment::class)
    internal abstract fun injectorFactory(builder: RecentlyAddedFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
