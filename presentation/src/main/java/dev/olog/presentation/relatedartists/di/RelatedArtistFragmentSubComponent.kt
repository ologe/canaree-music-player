package dev.olog.presentation.relatedartists.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.relatedartists.RelatedArtistFragment

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