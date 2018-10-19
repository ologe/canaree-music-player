package dev.olog.msc.presentation.library.categories.podcast.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.library.categories.podcast.CategoriesPodcastFragment

@Module(subcomponents = arrayOf(CategoriesPodcastFragmentSubComponent::class))
abstract class CategoriesPodcastFragmentInjector{

    @Binds
    @IntoMap
    @FragmentKey(CategoriesPodcastFragment::class)
    internal abstract fun injectorFactory(builder: CategoriesPodcastFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out androidx.fragment.app.Fragment>

}
