package dev.olog.msc.presentation.detail.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.base.FragmentXKey
import dev.olog.msc.presentation.detail.DetailFragment

@Module(subcomponents = arrayOf(DetailFragmentSubComponent::class))
abstract class DetailFragmentInjector {

    @Binds
    @IntoMap
    @FragmentXKey(DetailFragment::class)
    internal abstract fun injectorFactory(builder: DetailFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
