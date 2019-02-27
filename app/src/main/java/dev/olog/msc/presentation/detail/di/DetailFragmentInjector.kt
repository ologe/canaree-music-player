package dev.olog.msc.presentation.detail.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.detail.DetailFragment

@Module(subcomponents = arrayOf(DetailFragmentSubComponent::class))
abstract class DetailFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(DetailFragment::class)
    internal abstract fun injectorFactory(builder: DetailFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
