package dev.olog.msc.presentation.special.thanks.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.special.thanks.SpecialThanksFragment


@Module(subcomponents = arrayOf(SpecialThanksFragmentSubComponent::class))
abstract class SpecialThanksFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(SpecialThanksFragment::class)
    internal abstract fun injectorFactory(builder: SpecialThanksFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
