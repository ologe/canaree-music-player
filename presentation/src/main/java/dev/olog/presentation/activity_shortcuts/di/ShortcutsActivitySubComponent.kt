package dev.olog.presentation.activity_shortcuts.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_shortcuts.ShortcutsActivity
import dev.olog.presentation.dagger.PerActivity

@Subcomponent()
@PerActivity
interface ShortcutsActivitySubComponent : AndroidInjector<ShortcutsActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ShortcutsActivity>()

}