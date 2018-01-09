package dev.olog.presentation.activity_shortcuts.di

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.presentation.activity_shortcuts.ShortcutsActivity

@Module(subcomponents = arrayOf(ShortcutsActivitySubComponent::class))
abstract class ShortcutsActivityInjector {

    @Binds
    @IntoMap
    @ActivityKey(ShortcutsActivity::class)
    internal abstract fun injectorFactory(builder: ShortcutsActivitySubComponent.Builder)
            : AndroidInjector.Factory<out Activity>

}