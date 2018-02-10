package dev.olog.msc.presentation.related.artists.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment

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