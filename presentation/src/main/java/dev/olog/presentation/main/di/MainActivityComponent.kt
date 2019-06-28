package dev.olog.presentation.main.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dev.olog.injection.CoreComponent
import dev.olog.presentation.ViewModelModule
import dev.olog.presentation.createplaylist.di.PlaylistTracksChooserInjector
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.detail.di.DetailFragmentInjector
import dev.olog.presentation.folder.tree.di.FolderTreeFragmentModule
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.model.PresentationModelModule
import dev.olog.presentation.player.di.PlayerFragmentModule
import dev.olog.presentation.queue.di.PlayingQueueFragmentInjector
import dev.olog.presentation.search.di.SearchFragmentInjector
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
//        RecentlyAddedFragmentInjector::class,
//        RelatedArtistFragmentInjector::class,
        SearchFragmentInjector::class,
        PlayingQueueFragmentInjector::class,
        PlaylistTracksChooserInjector::class

//        EditTrackFragmentInjector::class, TODO
//        EditAlbumFragmentInjector::class,
//        EditArtistFragmentInjector::class,

        // dialogs
//        AddFavoriteDialogInjector::class, TODO
//        PlayNextDialogInjector::class,
//        PlayLaterDialogInjector::class,
//        SetRingtoneDialogInjector::class,
//        RenameDialogInjector::class,
//        ClearPlaylistDialogInjector::class,
//        DeleteDialogInjector::class,
//        NewPlaylistDialogInjector::class,
//        RemoveDuplicatesDialogInjector::class
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