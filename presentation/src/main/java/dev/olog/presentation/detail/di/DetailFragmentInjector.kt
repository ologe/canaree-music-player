package dev.olog.presentation.detail.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.feature.detail.DetailFragment

@Module(subcomponents = [DetailFragmentSubComponent::class])
abstract class DetailFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(DetailFragment::class)
    internal abstract fun injectorFactory(builder: DetailFragmentSubComponent.Factory)
            : AndroidInjector.Factory<*>

}
