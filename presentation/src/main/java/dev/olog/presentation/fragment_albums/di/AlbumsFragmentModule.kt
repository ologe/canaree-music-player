package dev.olog.presentation.fragment_albums.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Playlist
import dev.olog.domain.interactor.detail.siblings.*
import dev.olog.presentation.R
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_albums.AlbumsFragment
import dev.olog.presentation.fragment_albums.AlbumsFragmentViewModel
import dev.olog.presentation.fragment_albums.AlbumsFragmentViewModelFactory
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.groupMap
import io.reactivex.Flowable

@Module
class AlbumsFragmentModule(
       private val fragment: AlbumsFragment
) {

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideMediaId(): String {
        return fragment.arguments!!.getString(AlbumsFragment.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    internal fun provideViewModel(factory: AlbumsFragmentViewModelFactory): AlbumsFragmentViewModel {

        return ViewModelProviders.of(fragment, factory).get(AlbumsFragmentViewModel::class.java)
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_FOLDER)
    internal fun provideFolderData(
            resources: Resources,
            mediaId: String,
            useCase: GetFolderSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toAlbumsDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_PLAYLIST)
    internal fun providePlaylistData(
            resources: Resources,
            mediaId: String,
            useCase: GetPlaylistSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toAlbumsDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALBUM)
    internal fun provideAlbumData(
            resources: Resources,
            mediaId: String,
            useCase: GetAlbumSiblingsByAlbumUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toAlbumsDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ARTIST)
    internal fun provideArtistData(
            resources: Resources,
            mediaId: String,
            useCase: GetAlbumSiblingsByArtistUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toAlbumsDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_GENRE)
    internal fun provideGenreData(
            resources: Resources,
            mediaId: String,
            useCase: GetGenreSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toAlbumsDetailDisplayableItem(resources) }
    }
}


private fun Folder.toAlbumsDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaIdHelper.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    )
}

private fun Playlist.toAlbumsDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaIdHelper.playlistId(id),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    )
}

private fun Album.toAlbumsDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaIdHelper.albumId(id),
            title,
            resources.getQuantityString(R.plurals.song_count, this.songs, this.songs).toLowerCase(),
            image
    )
}

private fun Genre.toAlbumsDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_albums,
            MediaIdHelper.genreId(id),
            name.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    )
}
