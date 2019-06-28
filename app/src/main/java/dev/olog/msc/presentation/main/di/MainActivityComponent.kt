package dev.olog.msc.presentation.main.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dev.olog.injection.CoreComponent
import dev.olog.msc.presentation.ViewModelModule
import dev.olog.presentation.detail.di.DetailFragmentInjector
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
import dev.olog.msc.presentation.player.di.PlayerFragmentModule
import dev.olog.presentation.queue.di.PlayingQueueFragmentInjector
import dev.olog.msc.presentation.playlist.track.chooser.di.PlaylistTracksChooserInjector
import dev.olog.msc.presentation.recently.added.di.RecentlyAddedFragmentInjector
import dev.olog.msc.presentation.related.artists.di.RelatedArtistFragmentInjector
import dev.olog.presentation.search.di.SearchFragmentInjector
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.model.PresentationModelModule
import dev.olog.presentation.tab.di.TabFragmentInjector

fun MainActivity.inject() {
    DaggerMainActivityComponent.factory()
        .create(this, CoreComponent.coreComponent(application))
        .inject(this)
}

@Component(
    modules = arrayOf(
        PresentationModelModule::class,

        AndroidInjectionModule::class,
        ViewModelModule::class,
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
    ), dependencies = [CoreComponent::class]
)
@PerActivity
interface MainActivityComponent {

    fun inject(instance: MainActivity)

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance instance: MainActivity, component: CoreComponent): MainActivityComponent
    }

}