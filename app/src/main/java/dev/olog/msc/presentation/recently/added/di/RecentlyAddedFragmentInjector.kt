package dev.olog.msc.presentation.recently.added.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment

@Module(subcomponents = arrayOf(RecentlyAddedFragmentSubComponent::class))
abstract class RecentlyAddedFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(RecentlyAddedFragment::class)
    internal abstract fun injectorFactory(builder: RecentlyAddedFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
