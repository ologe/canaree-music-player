package dev.olog.msc.presentation.edit.album.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.msc.presentation.edit.album.EditAlbumFragment

@Subcomponent(modules = arrayOf(
        EditAlbumFragmentModule::class
))
@PerFragment
interface EditAlbumFragmentSubComponent : AndroidInjector<EditAlbumFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditAlbumFragment>() {

        abstract fun module(module: EditAlbumFragmentModule): Builder

        override fun seedInstance(instance: EditAlbumFragment) {
            module(EditAlbumFragmentModule(instance))
        }
    }

}