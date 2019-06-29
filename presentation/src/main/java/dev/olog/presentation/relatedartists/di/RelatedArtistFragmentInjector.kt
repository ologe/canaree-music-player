package dev.olog.presentation.relatedartists.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.relatedartists.RelatedArtistFragment

@Module(subcomponents = arrayOf(RelatedArtistFragmentSubComponent::class))
abstract class RelatedArtistFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(RelatedArtistFragment::class)
    internal abstract fun injectorFactory(builder: RelatedArtistFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
