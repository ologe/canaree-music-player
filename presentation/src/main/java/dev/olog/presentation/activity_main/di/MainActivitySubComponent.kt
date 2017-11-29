package dev.olog.presentation.activity_main.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.dialog_entry.di.DialogItemInjector
import dev.olog.presentation.fragment_detail.di.DetailFragmentInjector
import dev.olog.presentation.fragment_mini_player.di.MiniPlayerFragmentInjector
import dev.olog.presentation.fragment_player.di.PlayerFragmentInjector
import dev.olog.presentation.fragment_queue.di.PlayingQueueInjector
import dev.olog.presentation.fragment_related_artist.di.RelatedArtistFragmentInjector
import dev.olog.presentation.fragment_tab.di.TabFragmentInjector
import dev.olog.presentation.navigation.NavigatorModule

@Subcomponent(modules = arrayOf(
        MainActivityModule::class,
        NavigatorModule::class,

        // fragments
        TabFragmentInjector::class,
        DetailFragmentInjector::class,
        PlayerFragmentInjector::class,
        MiniPlayerFragmentInjector::class,
        RelatedArtistFragmentInjector::class,
        PlayingQueueInjector::class,
        DialogItemInjector::class
))
@PerActivity
interface MainActivitySubComponent :AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>() {

        abstract fun module(module: MainActivityModule): Builder

        override fun seedInstance(instance: MainActivity) {
            module(MainActivityModule(instance))
        }
    }

}