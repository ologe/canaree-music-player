//package dev.olog.presentation.dialog_add_playlist.di
//
//import dagger.Subcomponent
//import dagger.android.AndroidInjector
//import dev.olog.presentation.dagger.PerFragment
//import dev.olog.presentation.dialog_add_playlist.AddPlaylistDialog
//
//
//@Subcomponent(modules = arrayOf(
//        AddPlaylistDialogModule::class
//))
//@PerFragment
//interface AddPlaylistDialogSubComponent : AndroidInjector<AddPlaylistDialog> {
//
//    @Subcomponent.Builder
//    abstract class Builder : AndroidInjector.Builder<AddPlaylistDialog>() {
//
//        abstract fun module(module: AddPlaylistDialogModule): Builder
//
//        override fun seedInstance(instance: AddPlaylistDialog) {
//            module(AddPlaylistDialogModule(instance))
//        }
//    }
//
//}