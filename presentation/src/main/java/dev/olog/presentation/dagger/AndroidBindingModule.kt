package dev.olog.presentation.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.dialog.FolderDialog

@Module
abstract class AndroidBindingModule {

    @ContributesAndroidInjector
    abstract fun folderDialog(): FolderDialog

}
