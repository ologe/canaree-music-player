package dev.olog.msc.presentation.licenses.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.licenses.LicensesFragment


@Module(subcomponents = arrayOf(LicensesFragmentSubComponent::class))
abstract class LicensesFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(LicensesFragment::class)
    internal abstract fun injectorFactory(builder: LicensesFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
