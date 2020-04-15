package dev.olog.presentation.playlist.chooser.di

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.navigation.dagger.ActivityKey
import dev.olog.navigation.screens.Activities
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.playlist.chooser.PlaylistChooserActivity

class FeaturePlaylistChooserDagger {

    @Subcomponent(modules = [PlaylistChooserActivityModule::class])
    @FeatureScope
    internal interface Graph : AndroidInjector<PlaylistChooserActivity> {

        @Subcomponent.Factory
        interface Factory : AndroidInjector.Factory<PlaylistChooserActivity>

    }

    @Module(subcomponents = [Graph::class])
    abstract class AppModule {

        @Binds
        @IntoMap
        @ClassKey(PlaylistChooserActivity::class)
        internal abstract fun provideFactory(factory: Graph.Factory): AndroidInjector.Factory<*>

        companion object {

            @Provides
            @IntoMap
            @ActivityKey(Activities.PLAYLIST_CHOOSER)
            internal fun provideChooser(): Class<out Activity> {
                return MainActivity::class.java
            }

        }

    }

}

