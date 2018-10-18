package dev.olog.msc.presentation.licenses.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.base.FragmentXKey
import dev.olog.msc.presentation.licenses.LicensesFragment


@Module(subcomponents = arrayOf(LicensesFragmentSubComponent::class))
abstract class LicensesFragmentInjector {

    @Binds
    @IntoMap
    @FragmentXKey(LicensesFragment::class)
    internal abstract fun injectorFactory(builder: LicensesFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
