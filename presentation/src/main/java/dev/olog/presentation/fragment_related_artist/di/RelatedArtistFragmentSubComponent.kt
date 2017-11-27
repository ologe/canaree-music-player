package dev.olog.presentation.fragment_related_artist.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_related_artist.RelatedArtistFragment

@Subcomponent(modules = arrayOf(
        RelatedArtistFragmentModule::class
))
@PerFragment
interface RelatedArtistFragmentSubComponent : AndroidInjector<RelatedArtistFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<RelatedArtistFragment>() {

        abstract fun module(module: RelatedArtistFragmentModule): Builder

        override fun seedInstance(instance: RelatedArtistFragment) {
            module(RelatedArtistFragmentModule(instance))
        }
    }

}