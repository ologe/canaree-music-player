package dev.olog.presentation.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.dialog.DialogItemFragment

@Module
abstract class AndroidBindingModule {

    @ContributesAndroidInjector
    abstract fun folderDialog(): DialogItemFragment

}
