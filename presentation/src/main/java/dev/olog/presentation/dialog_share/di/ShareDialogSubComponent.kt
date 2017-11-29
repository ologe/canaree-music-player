package dev.olog.presentation.dialog_share.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.dialog_share.ShareDialog


@Subcomponent(modules = arrayOf(
        ShareDialogModule::class
))
@PerFragment
interface ShareDialogSubComponent : AndroidInjector<ShareDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ShareDialog>() {

        abstract fun module(module: ShareDialogModule): Builder

        override fun seedInstance(instance: ShareDialog) {
            module(ShareDialogModule(instance))
        }
    }

}