package dev.olog.presentation.main.di

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.presentation.createplaylist.di.CreatePlaylistFragmentInjector
import dev.olog.presentation.dialogs.DialogModule
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.model.PresentationModelModule
import dev.olog.presentation.recentlyadded.di.RecentlyAddedFragmentInjector
import dev.olog.presentation.relatedartists.di.RelatedArtistFragmentInjector

class FeatureMainActivityDagger {

    @Subcomponent(
        modules = [
            PresentationModelModule::class,

            MainActivityModule::class,
            MainActivityFragmentsModule::class,
//
//        // fragments
            RecentlyAddedFragmentInjector::class,
            RelatedArtistFragmentInjector::class,
            CreatePlaylistFragmentInjector::class,

            DialogModule::class
        ]
    )
    @FeatureScope
    internal interface Graph : AndroidInjector<MainActivity> {

        @Subcomponent.Factory
        interface Factory : AndroidInjector.Factory<MainActivity>

    }

    @Module(subcomponents = [Graph::class])
    abstract class AppModule {

        @Binds
        @IntoMap
        @ClassKey(MainActivity::class)
        internal abstract fun provideFactory(factory: Graph.Factory): AndroidInjector.Factory<*>

    }

}