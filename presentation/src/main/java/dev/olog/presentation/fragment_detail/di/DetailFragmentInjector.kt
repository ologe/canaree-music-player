package dev.olog.presentation.fragment_detail.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.fragment_detail.DetailFragment

@Module(subcomponents = arrayOf(DetailFragmentSubComponent::class))
abstract class DetailFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(DetailFragment::class)
    internal abstract fun injectorFactory(builder: DetailFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
