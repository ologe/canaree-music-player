package dev.olog.presentation.activity_main.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.dialog_add_favorite.di.AddFavoriteDialogInjector
import dev.olog.presentation.dialog_add_queue.di.AddQueueDialogInjector
import dev.olog.presentation.dialog_clear_playlist.di.ClearPlaylistDialogInjector
import dev.olog.presentation.dialog_delete.di.DeleteDialogInjector
import dev.olog.presentation.dialog_new_playlist.di.NewPlaylistDialogInjector
import dev.olog.presentation.dialog_rename.di.RenameDialogInjector
import dev.olog.presentation.dialog_set_ringtone.di.SetRingtoneDialogInjector
import dev.olog.presentation.fragment_albums.di.AlbumsFragmentInjector
import dev.olog.presentation.fragment_detail.di.DetailFragmentInjector
import dev.olog.presentation.fragment_edit_info.di.EditInfoFragmentInjector
import dev.olog.presentation.fragment_mini_player.di.MiniPlayerFragmentInjector
import dev.olog.presentation.fragment_player.di.PlayerFragmentInjector
import dev.olog.presentation.fragment_playing_queue.di.PlayingQueueFragmentInjector
import dev.olog.presentation.fragment_recently_added.di.RecentlyAddedFragmentInjector
import dev.olog.presentation.fragment_related_artist.di.RelatedArtistFragmentInjector
import dev.olog.presentation.fragment_search.di.SearchFragmentInjector
import dev.olog.presentation.fragment_tab.di.TabFragmentInjector
import dev.olog.presentation.navigation.NavigatorModule
import dev.olog.presentation.pro.ProModule

@Subcomponent(modules = arrayOf(
        MainActivityModule::class,
        NavigatorModule::class,
        ProModule::class,

        // fragments
        TabFragmentInjector::class,
        DetailFragmentInjector::class,
        PlayerFragmentInjector::class,
        RecentlyAddedFragmentInjector::class,
        RelatedArtistFragmentInjector::class,
        AlbumsFragmentInjector::class,
        MiniPlayerFragmentInjector::class,
        SearchFragmentInjector::class,
        PlayingQueueFragmentInjector::class,
        EditInfoFragmentInjector::class,

        // dialogs
        AddFavoriteDialogInjector::class,
        NewPlaylistDialogInjector::class,
        AddQueueDialogInjector::class,
        SetRingtoneDialogInjector::class,
        RenameDialogInjector::class,
        ClearPlaylistDialogInjector::class,
        DeleteDialogInjector::class
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