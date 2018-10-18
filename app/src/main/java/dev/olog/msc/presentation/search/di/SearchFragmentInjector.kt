package dev.olog.msc.presentation.search.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.base.FragmentXKey
import dev.olog.msc.presentation.search.SearchFragment


@Module(subcomponents = arrayOf(SearchFragmentSubComponent::class))
abstract class SearchFragmentInjector {

    @Binds
    @IntoMap
    @FragmentXKey(SearchFragment::class)
    internal abstract fun injectorFactory(builder: SearchFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
