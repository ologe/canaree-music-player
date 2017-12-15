package dev.olog.presentation.fragment_about.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.fragment_about.AboutFragment

@Module(subcomponents = arrayOf(AboutFragmentSubComponent::class))
abstract class AboutFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(AboutFragment::class)
    internal abstract fun injectorFactory(builder: AboutFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
