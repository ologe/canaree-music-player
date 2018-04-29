package dev.olog.msc.presentation.library.folder.tree.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragment

@Subcomponent(modules = arrayOf(
        FolderTreeFragmentModule::class
))
@PerFragment
interface FolderTreeFragmentSubComponent : AndroidInjector<FolderTreeFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<FolderTreeFragment>() {

        abstract fun module(module: FolderTreeFragmentModule): Builder

        override fun seedInstance(instance: FolderTreeFragment) {
            module(FolderTreeFragmentModule(instance))
        }
    }

}