package dev.olog.presentation.fragment_albums.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_albums.AlbumsFragment


@Subcomponent(modules = arrayOf(
        AlbumsFragmentModule::class
))
@PerFragment
interface AlbumsFragmentSubComponent : AndroidInjector<AlbumsFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AlbumsFragment>() {

        abstract fun module(module: AlbumsFragmentModule): Builder

        override fun seedInstance(instance: AlbumsFragment) {
            module(AlbumsFragmentModule(instance))
        }
    }

}