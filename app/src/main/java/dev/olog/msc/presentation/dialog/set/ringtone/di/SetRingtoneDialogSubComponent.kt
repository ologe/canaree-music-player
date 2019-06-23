package dev.olog.msc.presentation.dialog.set.ringtone.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.msc.presentation.dialog.set.ringtone.SetRingtoneDialog

@Subcomponent(modules = arrayOf(
        SetRingtoneDialogModule::class
))
@PerFragment
interface SetRingtoneDialogSubComponent : AndroidInjector<SetRingtoneDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SetRingtoneDialog>() {

        abstract fun module(module: SetRingtoneDialogModule): Builder

        override fun seedInstance(instance: SetRingtoneDialog) {
            module(SetRingtoneDialogModule(instance))
        }
    }

}