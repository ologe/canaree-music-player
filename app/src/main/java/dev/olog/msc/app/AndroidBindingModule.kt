package dev.olog.msc.app

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.shortcuts.ShortcutsActivity

@Module
abstract class AndroidBindingModule {

    @ContributesAndroidInjector
    abstract fun provideShortcustActivity(): ShortcutsActivity

}