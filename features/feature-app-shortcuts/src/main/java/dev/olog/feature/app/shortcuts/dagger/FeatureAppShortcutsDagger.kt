package dev.olog.feature.app.shortcuts.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.app.shortcuts.AppShortcuts
import dev.olog.feature.app.shortcuts.AppShortcutsImp
import dev.olog.feature.app.shortcuts.ShortcutsActivity
import dev.olog.navigation.dagger.ActivityKey
import dev.olog.navigation.screens.Activities
import javax.inject.Singleton

class FeatureAppShortcutsDagger {

    @Subcomponent
    @FeatureScope
    internal interface Graph : AndroidInjector<ShortcutsActivity> {

        @Subcomponent.Factory
        interface Factory : AndroidInjector.Factory<ShortcutsActivity>

    }

    @Module(subcomponents = [Graph::class])
    abstract class AppModule {

        @Binds
        @Singleton
        internal abstract fun provideAppShortcuts(impl: AppShortcutsImp): AppShortcuts

        @Binds
        @IntoMap
        @ClassKey(ShortcutsActivity::class)
        internal abstract fun provideFactory(factory: Graph.Factory): AndroidInjector.Factory<*>

        companion object {

            @Provides
            @IntoMap
            @ActivityKey(Activities.SHORTCUTS)
            internal fun provideActivity(): Class<out Activity> {
                return ShortcutsActivity::class.java
            }

        }

    }

}