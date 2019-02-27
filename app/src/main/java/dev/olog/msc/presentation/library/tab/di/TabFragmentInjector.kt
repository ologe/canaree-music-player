package dev.olog.msc.presentation.library.tab.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.library.tab.TabFragment


@Module(subcomponents = arrayOf(TabFragmentSubComponent::class))
abstract class TabFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(TabFragment::class)
    internal abstract fun injectorFactory(builder: TabFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
