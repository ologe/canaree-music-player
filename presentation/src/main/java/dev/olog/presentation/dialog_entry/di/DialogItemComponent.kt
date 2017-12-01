package dev.olog.presentation.dialog_entry.di

import dagger.Component
import dev.olog.presentation.activity_main.di.MainActivitySubComponent
import dev.olog.presentation.dialog_entry.DialogItemFragment
import dev.olog.presentation.dialog_entry.DialogUseCasesModule
import dev.olog.presentation.dialog_entry.ItemModule

@Component(modules = arrayOf(
        DialogUseCasesModule::class,
        ItemModule::class
), dependencies = arrayOf(
        MainActivitySubComponent::class
))
interface DialogItemComponent {

    fun inject(dialog: DialogItemFragment)

}