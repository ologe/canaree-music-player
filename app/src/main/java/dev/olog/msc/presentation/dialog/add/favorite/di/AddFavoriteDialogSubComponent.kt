package dev.olog.msc.presentation.dialog.add.favorite.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerFragment
import dev.olog.msc.presentation.dialog.add.favorite.AddFavoriteDialog

@Subcomponent(modules = arrayOf(
        AddFavoriteDialogModule::class
))
@PerFragment
interface AddFavoriteDialogSubComponent : AndroidInjector<AddFavoriteDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AddFavoriteDialog>() {

        abstract fun module(module: AddFavoriteDialogModule): Builder

        override fun seedInstance(instance: AddFavoriteDialog) {
            module(AddFavoriteDialogModule(instance))
        }
    }

}