package dev.olog.presentation.playlist.chooser.di

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
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

    }

}

