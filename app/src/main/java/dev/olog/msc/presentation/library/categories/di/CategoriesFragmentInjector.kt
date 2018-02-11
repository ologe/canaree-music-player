package dev.olog.msc.presentation.library.categories.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.library.categories.CategoriesFragment

@Module(subcomponents = arrayOf(CategoriesFragmentSubComponent::class))
abstract class CategoriesFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(CategoriesFragment::class)
    internal abstract fun injectorFactory(builder: CategoriesFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
