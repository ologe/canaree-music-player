package dev.olog.presentation.fragment_tab.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.fragment_tab.TabFragment


@Module(subcomponents = arrayOf(TabFragmentSubComponent::class))
abstract class TabFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(TabFragment::class)
    internal abstract fun injectorFactory(builder: TabFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
