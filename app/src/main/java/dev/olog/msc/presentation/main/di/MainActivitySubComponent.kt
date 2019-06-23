package dev.olog.msc.presentation.main.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerActivity
import dev.olog.msc.presentation.detail.di.DetailFragmentInjector
import dev.olog.msc.presentation.dialog.add.favorite.di.AddFavoriteDialogInjector
import dev.olog.msc.presentation.dialog.clear.playlist.di.ClearPlaylistDialogInjector
import dev.olog.msc.presentation.dialog.create.playlist.di.NewPlaylistDialogInjector
import dev.olog.msc.presentation.dialog.delete.di.DeleteDialogInjector
import dev.olog.msc.presentation.dialog.play.later.di.PlayLaterDialogInjector
import dev.olog.msc.presentation.dialog.play.next.di.PlayNextDialogInjector
import dev.olog.msc.presentation.dialog.remove.duplicates.di.RemoveDuplicatesDialogInjector
import dev.olog.msc.presentation.dialog.rename.di.RenameDialogInjector
import dev.olog.msc.presentation.dialog.set.ringtone.di.SetRingtoneDialogInjector
import dev.olog.msc.presentation.edit.album.di.EditAlbumFragmentInjector
import dev.olog.msc.presentation.edit.artist.di.EditArtistFragmentInjector
import dev.olog.msc.presentation.edit.track.di.EditTrackFragmentInjector
import dev.olog.msc.presentation.library.folder.tree.di.FolderTreeFragmentModule
import dev.olog.presentation.tab.di.TabFragmentInjector
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.player.di.PlayerFragmentModule
import dev.olog.msc.presentation.playing.queue.di.PlayingQueueFragmentInjector
import dev.olog.msc.presentation.playlist.track.chooser.di.PlaylistTracksChooserInjector
import dev.olog.msc.presentation.recently.added.di.RecentlyAddedFragmentInjector
import dev.olog.msc.presentation.related.artists.di.RelatedArtistFragmentInjector
import dev.olog.msc.presentation.search.di.SearchFragmentInjector

@Subcomponent(modules = arrayOf(
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
        EditTrackFragmentInjector::class,
        EditAlbumFragmentInjector::class,
        EditArtistFragmentInjector::class,
        PlaylistTracksChooserInjector::class,

        // dialogs
        AddFavoriteDialogInjector::class,
        PlayNextDialogInjector::class,
        PlayLaterDialogInjector::class,
        SetRingtoneDialogInjector::class,
        RenameDialogInjector::class,
        ClearPlaylistDialogInjector::class,
        DeleteDialogInjector::class,
        NewPlaylistDialogInjector::class,
        RemoveDuplicatesDialogInjector::class
))
@PerActivity
interface MainActivitySubComponent : AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>() {

        abstract fun module(module: MainActivityModule): Builder

        override fun seedInstance(instance: MainActivity) {
            module(MainActivityModule(instance))
        }
    }

}