package dev.olog.msc.presentation.shortcuts.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerActivity
import dev.olog.msc.presentation.shortcuts.ShortcutsActivity

@Subcomponent()
@PerActivity
interface ShortcutsActivitySubComponent : AndroidInjector<ShortcutsActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ShortcutsActivity>()

}