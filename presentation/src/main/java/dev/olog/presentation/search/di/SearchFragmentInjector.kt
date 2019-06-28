package dev.olog.presentation.search.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.search.SearchFragment


@Module(subcomponents = arrayOf(SearchFragmentSubComponent::class))
abstract class SearchFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(SearchFragment::class)
    internal abstract fun injectorFactory(builder: SearchFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
