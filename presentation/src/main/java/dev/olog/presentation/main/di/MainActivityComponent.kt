package dev.olog.presentation.main.di

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.presentation.about.di.AboutFragmentModule
import dev.olog.presentation.createplaylist.di.CreatePlaylistFragmentInjector
import dev.olog.presentation.detail.di.DetailFragmentInjector
import dev.olog.presentation.dialogs.DialogModule
import dev.olog.presentation.edit.di.EditItemModule
import dev.olog.presentation.equalizer.EqualizerModule
import dev.olog.presentation.folder.tree.di.FolderTreeFragmentModule
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.model.PresentationModelModule
import dev.olog.presentation.player.di.PlayerFragmentModule
import dev.olog.presentation.prefs.di.SettingsFragmentsModule
import dev.olog.presentation.queue.di.PlayingQueueFragmentInjector
import dev.olog.presentation.recentlyadded.di.RecentlyAddedFragmentInjector
import dev.olog.presentation.relatedartists.di.RelatedArtistFragmentInjector
import dev.olog.presentation.search.di.SearchFragmentInjector
import dev.olog.presentation.tab.di.TabFragmentInjector

class FeatureMainActivityDagger {

    @Subcomponent(
        modules = [
            PresentationModelModule::class,

            MainActivityModule::class,
            MainActivityFragmentsModule::class,
//
//        // fragments
            TabFragmentInjector::class,
            FolderTreeFragmentModule::class,
            DetailFragmentInjector::class,
            PlayerFragmentModule::class,
            RecentlyAddedFragmentInjector::class,
            RelatedArtistFragmentInjector::class,
            SearchFragmentInjector::class,
            PlayingQueueFragmentInjector::class,
            CreatePlaylistFragmentInjector::class,
            EqualizerModule::class,

            SettingsFragmentsModule::class,

            EditItemModule::class,
            AboutFragmentModule::class,

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