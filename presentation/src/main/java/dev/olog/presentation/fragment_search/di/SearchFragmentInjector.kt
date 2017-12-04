package dev.olog.presentation.fragment_search.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.fragment_search.SearchFragment


@Module(subcomponents = arrayOf(SearchFragmentSubComponent::class))
abstract class SearchFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(SearchFragment::class)
    internal abstract fun injectorFactory(builder: SearchFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
