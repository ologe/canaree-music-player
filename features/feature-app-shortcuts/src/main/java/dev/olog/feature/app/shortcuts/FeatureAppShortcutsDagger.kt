package dev.olog.feature.app.shortcuts

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.navigation.dagger.ActivityKey
import dev.olog.navigation.screens.Activities

class FeatureAppShortcutsDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector
        abstract fun provideActivity(): ShortcutsActivity

        companion object {

            @Provides
            @IntoMap
            @ActivityKey(Activities.SHORTCUTS)
            fun provideActivity(): Class<out Activity> {
                return ShortcutsActivity::class.java
            }

        }

    }

}