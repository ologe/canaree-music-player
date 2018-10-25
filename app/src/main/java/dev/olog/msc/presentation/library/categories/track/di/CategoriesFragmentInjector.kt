package dev.olog.msc.presentation.library.categories.track.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.library.categories.track.CategoriesFragment

@Module(subcomponents = arrayOf(CategoriesFragmentSubComponent::class))
abstract class CategoriesFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(CategoriesFragment::class)
    internal abstract fun injectorFactory(builder: CategoriesFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
