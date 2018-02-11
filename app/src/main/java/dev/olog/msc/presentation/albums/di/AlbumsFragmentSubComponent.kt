package dev.olog.msc.presentation.albums.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.albums.AlbumsFragment


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