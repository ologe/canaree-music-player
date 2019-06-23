package dev.olog.msc.presentation.dialog.play.next.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.msc.presentation.dialog.play.next.PlayNextDialog

@Subcomponent(modules = arrayOf(
        PlayNextDialogModule::class
))
@PerFragment
interface PlayNextDialogSubComponent : AndroidInjector<PlayNextDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PlayNextDialog>() {

        abstract fun module(module: PlayNextDialogModule): Builder

        override fun seedInstance(instance: PlayNextDialog) {
            module(PlayNextDialogModule(instance))
        }
    }

}