package dev.olog.msc.presentation.dialog.add.queue.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerFragment
import dev.olog.msc.presentation.dialog.add.queue.AddQueueDialog

@Subcomponent(modules = arrayOf(
        AddQueueDialogModule::class
))
@PerFragment
interface AddQueueDialogSubComponent : AndroidInjector<AddQueueDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AddQueueDialog>() {

        abstract fun module(module: AddQueueDialogModule): Builder

        override fun seedInstance(instance: AddQueueDialog) {
            module(AddQueueDialogModule(instance))
        }
    }

}