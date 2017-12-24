package dev.olog.presentation.fragment_detail.recently_added.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.fragment_detail.recently_added.DetailRecentlyAddedFragment

@Module(subcomponents = arrayOf(DetailRecentlyAddedFragmentSubComponent::class))
abstract class DetailRecentlyAddedFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(DetailRecentlyAddedFragment::class)
    internal abstract fun injectorFactory(builder: DetailRecentlyAddedFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
