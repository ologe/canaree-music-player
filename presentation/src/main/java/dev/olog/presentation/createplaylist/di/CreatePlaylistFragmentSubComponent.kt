package dev.olog.presentation.createplaylist.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.createplaylist.CreatePlaylistFragment


@Subcomponent(modules = arrayOf(
        CreatePlaylistFragmentModule::class
))
@PerFragment
interface CreatePlaylistFragmentSubComponent : AndroidInjector<CreatePlaylistFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<CreatePlaylistFragment>() {

        abstract fun module(module: CreatePlaylistFragmentModule): Builder

        override fun seedInstance(instance: CreatePlaylistFragment) {
            module(
                CreatePlaylistFragmentModule(
                    instance
                )
            )
        }
    }

}