package dev.olog.presentation.dialog_delete.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.dialog_delete.DeleteDialog

@Subcomponent(modules = arrayOf(
        DeleteDialogModule::class
))
@PerFragment
interface DeleteDialogSubComponent : AndroidInjector<DeleteDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DeleteDialog>() {

        abstract fun module(module: DeleteDialogModule): Builder

        override fun seedInstance(instance: DeleteDialog) {
            module(DeleteDialogModule(instance))
        }
    }

}